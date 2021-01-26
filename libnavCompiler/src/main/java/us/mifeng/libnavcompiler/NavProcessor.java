package us.mifeng.libnavcompiler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import sun.rmi.runtime.Log;
import us.mifeng.libnavannotation.ActivityDestination;
import us.mifeng.libnavannotation.FragmentDestination;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"us.mifeng.libnavannotation.FragmentDestination", "us.mifeng.libnavannotation.ActivityDestination"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NavProcessor extends AbstractProcessor {
    Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destination.json";
    private FileOutputStream fos = null;
    private OutputStreamWriter writer = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);
       // if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap();
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            handleDestination(activityElements, ActivityDestination.class, destMap);

            //将生成的json文件放到app/src/main/assets路径下
            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                //打印当前资源的地址
                messager.printMessage(Diagnostic.Kind.NOTE, "resourcePath=" + resourcePath);
                //由于默认的路径是在app/build/intermidiate/clasZ下，我们要将文件存在app/src/mian/assets/路径下，因此需要改路径
                //首先获取app/ 这个路径
                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                //然后拼接app/src/main/assets路径
                String assetsPath = appPath + "/src/main/assets";

                File file = new File(assetsPath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File assetsFile = new File(file, OUTPUT_FILE_NAME);
                if (assetsFile.exists()) {
                    assetsFile.delete();
                }
                assetsFile.createNewFile();
                //接着看如何将map中的数据放到file中去
                String content = JSON.toJSONString(destMap);
                fos = new FileOutputStream(assetsFile);
                writer = new OutputStreamWriter(fos, "utf-8");
                writer.write(content);
                writer.flush();
                System.out.println("这里走了吗？？？？" + content);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != writer) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
      //  }
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotation, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            String pageUrl = null;
            String claszName = typeElement.getQualifiedName().toString();
            int id = Math.abs(claszName.hashCode());
            boolean asStarter = false;
            boolean needLogin = false;
            boolean isFragmentDestination = false;
            Annotation annotation1 = typeElement.getAnnotation(annotation);
            if (annotation1 instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation1;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragmentDestination = true;

            } else if (annotation1 instanceof ActivityDestination) {
                ActivityDestination dest = (ActivityDestination) annotation1;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragmentDestination = false;
            }

            //赋值以后将以上的属性可以转载到HashMap当中去
            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不可以使用相同的url");
            } else {
                //将其组装成一个jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("needLogin", needLogin);
                jsonObject.put("asStarter", asStarter);
                jsonObject.put("pageUrl", pageUrl);
                jsonObject.put("claszName", claszName);
                jsonObject.put("isFragmentDestination", isFragmentDestination);
            }
        }
    }
}
