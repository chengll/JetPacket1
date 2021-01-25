package us.mifeng.libnavcompiler;


import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;

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

import us.mifeng.libnavannotation.ActivityDestination;
import us.mifeng.libnavannotation.FragmentDestination;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"us.mifeng.libnavannotation.FragmentDestination","us.mifeng.libnavannotation.ActivityDestination"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NavProcessor extends AbstractProcessor {
    Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager= processingEnvironment.getMessager();
         filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);
        if (null!=fragmentElements || null!=activityElements){
            HashMap<String, JSONObject> destMap=new HashMap();
            handleDestination(fragmentElements,FragmentDestination.class,destMap);
            handleDestination(activityElements,ActivityDestination.class,destMap);
        }
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<?extends Annotation> annotation, HashMap<String, JSONObject> destMap) {
        for (Element element: elements){
            TypeElement typeElement = (TypeElement) element;

        }
    }
}
