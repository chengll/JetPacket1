package us.mifeng.jetpacket1.navgraphy;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;
import java.util.Iterator;

import us.mifeng.jetpacket1.fixfragmentnavigator.FixFragmentNavigator;
import us.mifeng.jetpacket1.model.Destination;
import us.mifeng.jetpacket1.utils.AppConfig;
import us.mifeng.jetpacket1.utils.AppGlobals;

public class NavGraphBuilder {

    public static void build(NavController controller) {
        NavigatorProvider navigatorProvider = controller.getNavigatorProvider();
        //这里本来使用的是系统自带的方法，当我们重写了fragmentNavigator方法以后，我们要使用我们自定义的fixFragmentNavigator 代替
        FragmentNavigator fragmentNavigator = navigatorProvider.getNavigator(FragmentNavigator.class);
        ActivityNavigator activityNavigator = navigatorProvider.getNavigator(ActivityNavigator.class);
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(navigatorProvider));
        HashMap<String, Destination> destinationHashMap = AppConfig.getsDestinationMap();
        for (Destination destination : destinationHashMap.values()) {
            if (destination.isIsFisFragmentDestination()) {
                FragmentNavigator.Destination fragmentDestination = fragmentNavigator.createDestination();
                fragmentDestination.setClassName(destination.getClaszName());
                fragmentDestination.setId(destination.getId());
                fragmentDestination.addDeepLink(destination.getPageUrl());
                navGraph.addDestination(fragmentDestination);
            } else {
                ActivityNavigator.Destination activityDestination = activityNavigator.createDestination();
                activityDestination.setId(destination.getId());
                activityDestination.addDeepLink(destination.getPageUrl());
                activityDestination.setComponentName(new ComponentName(AppGlobals.getmApplilcation().getPackageName(), destination.getClaszName()));
                navGraph.addDestination(activityDestination);
            }

            if (destination.isAsStarter()) {
                navGraph.setStartDestination(destination.getId());
            }
            controller.setGraph(navGraph); //到这里我们的navGraph就构建好了，在MainActivity中使用即可

        }
    }

    //方法build2是使用优化后的FixFragmentNavigator来替代系统自带的Fragmentnavigator
    public static void build2(NavController controller, FragmentActivity activity, int containerId) {
        NavigatorProvider navigatorProvider = controller.getNavigatorProvider();
        //1.这里本来使用的是系统自带的方法，当我们重写了fragmentNavigator方法以后，我们要使用我们自定义的fixFragmentNavigator 代替
        //FragmentNavigator fragmentNavigator = navigatorProvider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity, activity.getSupportFragmentManager(), containerId);
        //2.将我们自定义的fixFragmentNavigator加入到provider这个HashMap中去
        navigatorProvider.addNavigator(fragmentNavigator);
        //3.在MainActivity中去测试，将一些必须传递的参数传递进来

        ActivityNavigator activityNavigator = navigatorProvider.getNavigator(ActivityNavigator.class);
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(navigatorProvider));
        HashMap<String, Destination> destinationHashMap = AppConfig.getsDestinationMap();
      /*
       遍历hashMap 通过迭代器的方式
      Iterator<Destination> iterator = destinationHashMap.values().iterator();
        while (iterator.hasNext()){
            Destination destination=iterator.next();
        }*/
        for (Destination destination : destinationHashMap.values()) {
            if (destination.isIsFisFragmentDestination()) {
                FragmentNavigator.Destination fragmentDestination = fragmentNavigator.createDestination();
                fragmentDestination.setClassName(destination.getClaszName());
                fragmentDestination.setId(destination.getId());
                fragmentDestination.addDeepLink(destination.getPageUrl());
                navGraph.addDestination(fragmentDestination);
            } else {
                ActivityNavigator.Destination activityDestination = activityNavigator.createDestination();
                activityDestination.setId(destination.getId());
                activityDestination.addDeepLink(destination.getPageUrl());
                activityDestination.setComponentName(new ComponentName(AppGlobals.getmApplilcation().getPackageName(), destination.getClaszName()));
                navGraph.addDestination(activityDestination);
            }

            if (destination.isAsStarter()) {
                navGraph.setStartDestination(destination.getId());
            }
            controller.setGraph(navGraph); //到这里我们的navGraph就构建好了，在MainActivity中使用即可

        }
    }
}
