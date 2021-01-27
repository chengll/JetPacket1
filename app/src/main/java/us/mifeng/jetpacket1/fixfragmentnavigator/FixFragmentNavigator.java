package us.mifeng.jetpacket1.fixfragmentnavigator;
/*
 * 在jetpacket中，fragment的切换都使用的是replace，而不是hide和show的方式，这样会导致
 * 每次加载时fragment都会被重新加载，因此我们通过该案例将fragment的切换修改为hide和show的方式
 *
 * */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;

@Navigator.Name("fixfragment")

public class FixFragmentNavigator extends FragmentNavigator {
    private static final String TAG = "FixFragmentNavigator";
    private Context context;
    private FragmentManager manager;
    private int containerId;
    private Fragment fragmentByTag;
    private boolean mIsPendingBackStackOperation = false;

    public FixFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.context = context;
        this.manager = manager;
        this.containerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {


        if (manager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = context.getPackageName() + className;
        }
        final Fragment frag = instantiateFragment(context, manager,
                className, args);
        frag.setArguments(args);
        final FragmentTransaction ft = manager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        //ft.replace(mContainerId, frag);
        //修改的部分为：
        Fragment fragment = manager.getPrimaryNavigationFragment(); //获取当前正在显示的fragment
        if (null != fragment) {
            ft.hide(fragment);
        }

        //判断当的fragment是否已经存在，如果不存在，则创建，否则让它显示出来
        fragmentByTag = manager.findFragmentByTag(String.valueOf(destination.getClassName()));
        if (null != fragmentByTag) {
            ft.show(fragmentByTag);
        } else {
            fragmentByTag = instantiateFragment(context, manager, className, args);
            fragmentByTag.setArguments(args);
            ft.add(containerId, fragmentByTag);
        }

        ft.setPrimaryNavigationFragment(fragmentByTag);
        ArrayDeque<Integer> mBackStack = null;

        //由源码可以知道mBackStack是管理fragment的数组栈，因此我们只能通过反射的方式来获取
        Field field = null;
        try {
            field = FragmentNavigator.class.getDeclaredField("mBackStack");
            field.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//都是重写修改的地方

        final @IdRes
        int destId = destination.getId();
        final boolean initialNavigation = mBackStack.isEmpty();
        // TODO Build first class singleTop behavior for fragments
        final boolean isSingleTopReplacement = navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId;

        boolean isAdded;
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size() > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                manager.popBackStack(
                        generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.addToBackStack(generateBackStackName(mBackStack.size(), destId));
                mIsPendingBackStackOperation = true;
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size() + 1, destId));
            mIsPendingBackStackOperation = true;
            isAdded = true;
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras = (Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mBackStack.add(destId);
            return destination;
        } else {
            return null;


        }

    }

    //重写自定义的方法， 写完以后我们要在NavGraphBuilder中去使用
    private String generateBackStackName(int backStack, int id) {
        return backStack + "-" + id;
    }

}
