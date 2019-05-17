package com.hai.jackie.router_processor;

import com.google.auto.service.AutoService;
import com.hai.jackie.router_annotations.Router;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class BindMetaDataProcessor extends AbstractProcessor {

    private Filer mFilter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFilter = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Router.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);
        createFile(elements);
        return true;
    }

    private void createFile(Set<? extends Element> elements) {
        FieldSpec fieldSpec = FieldSpec
                .builder(HashMap.class, "sHashMap", Modifier.PUBLIC, Modifier.STATIC)
                .build();
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class);
        methodBuilder.addStatement("sHashMap = new HashMap()");
        String packageName = "";
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            Router metaDataAnn = typeElement.getAnnotation(Router.class);
            methodBuilder.addStatement("sHashMap.put($S,$S)", metaDataAnn.value(), typeElement.getQualifiedName());
            if (packageName.equals("")) {
                packageName = typeElement.getQualifiedName().toString()
                        .replace("." + typeElement.getSimpleName(), "");
            }
        }

        TypeSpec type = TypeSpec
                .classBuilder("RouterFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(methodBuilder.build())
                .addField(fieldSpec)
                .build();
        JavaFile javaFile = JavaFile
                .builder(packageName + ".factory", type)
                .build();
        try {
            javaFile.writeTo(mFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
