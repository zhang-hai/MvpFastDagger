package org.harry.fastdagger.processor;

import org.harry.fastdagger.annotation.FastDagger;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("org.harry.fastdagger.annotation.FastDagger")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(value = {"fastDaggerIndex"})
public class FastDaggerAnnotationProcessor extends AbstractProcessor {
    public static final String OPTION_FAST_DAGGER_INDEX = "fastDaggerIndex";

    private final String PACKAGE_FORMAT = "package %s;\n\n";
    private final String IMPORT_FORMAT = "import %s;\n";
    private final String CLASS_FORMAT = "public class %s {\n";
    private final String INTERFACE_FORMAT = "public interface %s {\n";

    private Messager messager = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String index = processingEnv.getOptions().get(OPTION_FAST_DAGGER_INDEX);
        if (index == null || index.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "No option " + OPTION_FAST_DAGGER_INDEX +
                    " passed to annotation processor");
            return false;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FastDagger.class);
        for (Element element : elements){
            FastDagger fastDagger = element.getAnnotation(FastDagger.class);
            //生成module文件
            String moduleName = generateModuleFile(fastDagger,index);
            //生成component文件
            generateComponentFile(fastDagger,index,moduleName);
        }
        return true;
    }

    /**
     * 生成module文件
     * @param fastDagger
     * @param packageName
     * @return
     */
    private String generateModuleFile(FastDagger fastDagger,String packageName) {
        StringBuilder sb = new StringBuilder();
        //获取IxxMode和IxxView的简单类名
        TypeMirror modelValue = null;
        try {
            fastDagger.modelCls();
        }catch( MirroredTypeException mte ) {
            modelValue = mte.getTypeMirror();
        }
        TypeMirror viewValue = null;
        try {
            fastDagger.viewCls();
        }catch( MirroredTypeException mte ) {
            viewValue = mte.getTypeMirror();
        }
        TypeMirror scopeValue = null;
        try {
            fastDagger.scopeCls();
        }catch( MirroredTypeException mte ) {
            scopeValue = mte.getTypeMirror();
        }

        messager.printMessage(Diagnostic.Kind.NOTE,"fastDagger --> " + modelValue.toString());
        String simpleModelName = modelValue.toString().substring(modelValue.toString().lastIndexOf(".")+1);
        String simpleViewName = viewValue.toString().substring(viewValue.toString().lastIndexOf(".")+1);
        String simpleScopeName = scopeValue.toString().substring(scopeValue.toString().lastIndexOf(".")+1);
        String simpleModelImpName = simpleModelName.replace("I","") + "Imp";
        String moduleName = simpleModelName.replace("I","").replace("Model","")+"Module";
        String fullPackageName = String.format(Locale.CHINESE,"%s.di.module",packageName);
        String moduleFileName = String.format(Locale.CHINESE,"%s.%s",fullPackageName,moduleName);
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,moduleFileName.substring(0,moduleFileName.lastIndexOf("."))))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,modelValue.toString()))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,viewValue.toString()))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,modelValue.toString().substring(0,modelValue.toString().lastIndexOf(".")+1)+simpleModelImpName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Module"))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Provides"))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,scopeValue.toString()))
                .append("\n")
                .append("@Module\n")
                .append(String.format(Locale.CHINESE,CLASS_FORMAT,moduleName))
                .append(String.format(Locale.CHINESE,"\n\tprivate %s view;\n\n",simpleViewName))
                .append(String.format(Locale.CHINESE,"\tpublic %s(%s view) {\n\t\tthis.view = view;\n\t}\n\n",moduleName,simpleViewName))
                .append(String.format(Locale.CHINESE,"\t@%s\n\t@Provides\n\t%s provide%s(){\n\t\treturn this.view;\n\t}\n\n",simpleScopeName,simpleViewName,simpleViewName.replace("I","")))
                .append(String.format(Locale.CHINESE,"\t@%s\n\t@Provides\n\t%s provide%s(%s model){\n\t\treturn model;\n\t}\n\n",simpleScopeName,simpleModelName,simpleModelName.replace("I",""),simpleModelImpName))
                .append("}");

        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(moduleFileName);
            Writer writer = fileObject.openWriter();
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,"generate module file error : " + e.getMessage());
        }
        return moduleFileName;
    }

    /**
     * 生成Component类文件
     * @param fastDagger
     * @param packageName
     * @param moduleName
     */
    private void generateComponentFile(FastDagger fastDagger, String packageName, String moduleName) {
        TypeMirror activityValue = null;
        try {
            fastDagger.activityCls();
        }catch( MirroredTypeException mte ) {
            activityValue = mte.getTypeMirror();
        }
        TypeMirror scopeValue = null;
        try {
            fastDagger.scopeCls();
        }catch( MirroredTypeException mte ) {
            scopeValue = mte.getTypeMirror();
        }
        //activity或Fragment类的全路径
        String actFullName = activityValue.toString();
        //activity's or fragment's simple class name
        String actSimpleName = actFullName.substring(actFullName.lastIndexOf(".")+1);
        //module class' simple name
        String moduleSimpleName = moduleName.substring(moduleName.lastIndexOf(".")+1);

        String scopeFullName = scopeValue.toString();
        String scopeSimpleName = scopeFullName.substring(scopeFullName.lastIndexOf(".")+1);
        //要生成的component类的名字
        String componentSimpleName = moduleSimpleName.replace("Module","")+"Component";
        //要生成的component类的全路径名称
        String componentFullName = String.format("%s.di.component.%s",packageName,componentSimpleName);

        //获取到的依赖component
        List<? extends TypeMirror> dependenceList = null;
        try {
            fastDagger.dependencies();
        }catch (MirroredTypesException e){
            dependenceList = e.getTypeMirrors();
        }


        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,componentFullName.substring(0,componentFullName.lastIndexOf("."))))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,moduleName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,actFullName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,scopeFullName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Component"));
        for (TypeMirror mirror : dependenceList){
            messager.printMessage(Diagnostic.Kind.NOTE,"dependencies component --> "+mirror.toString());
            sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,mirror.toString()));
        }
        sb.append("\n")
                .append(String.format(Locale.CHINESE,"@%s\n",scopeSimpleName))
                .append(String.format(Locale.CHINESE,"@Component(modules = %s.class %s)\n",moduleSimpleName,getDependencies(dependenceList)))
                .append(String.format(Locale.CHINESE,INTERFACE_FORMAT,componentSimpleName))
                .append(String.format(Locale.CHINESE,"\tvoid inject(%s %s);\n",actSimpleName,changeClassNameToVar(actSimpleName)))
                .append("}");

        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(componentFullName);
            Writer writer = fileObject.openWriter();
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,"generate component file error : " + e.getMessage());
        }
    }

    /**
     * 根据列表获取依赖的Component类
     * @param dependenceList
     * @return
     */
    private String getDependencies(List<? extends TypeMirror> dependenceList) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (TypeMirror mirror : dependenceList){
            if(i == 0){
                sb.append(",dependencies = {");
            }
            sb.append(String.format(Locale.CHINESE,"%s.class",mirror.toString().substring(mirror.toString().lastIndexOf(".")+1)));
            if(i < dependenceList.size()-1){
                sb.append(",");
            }else {
                sb.append("}");
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * 将一个类名转换成驼峰命名的变量名称
     *
     * @param clsName 类名
     * @return 转换后的变量名字
     */
    private String changeClassNameToVar(String clsName){
        if(clsName.length() == 1){
            return clsName.toLowerCase();
        }
        return String.format(Locale.CHINESE,"%s%s",clsName.substring(0,1).toLowerCase(),clsName.substring(1));
    }

}
