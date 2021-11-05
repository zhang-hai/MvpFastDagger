package org.harry.fastdagger.processor;

import org.harry.fastdagger.annotation.FastDagger;
import org.harry.fastdagger.annotation.MvpFastDagger;

import java.io.File;
import java.io.FileWriter;
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("org.harry.fastdagger.annotation.MvpFastDagger")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(value = {"fastDaggerIndex","mvpSrcDir"})
public class MvpFastDaggerAnnotationProcessor extends AbstractProcessor {
    private static final String OPTION_FAST_DAGGER_INDEX = "fastDaggerIndex";
    private static final String OPTION_MVP_SRC = "mvpSrcDir";

    private final String PACKAGE_FORMAT = "package %s;\n\n";
    private final String IMPORT_FORMAT = "import %s;\n";
    private final String IMPORT_VIEW_FORMAT = "import %s.mvp.view%s.I%sView;\n";
    private final String IMPORT_MODEL_FORMAT = "import %s.mvp.model%s.I%sModel;\n";

    private final String CLASS_FORMAT = "public class %s %s %s{\n";
    private final String INTERFACE_FORMAT = "public interface %s %s{\n";


    //预留导入包的位置
    private final String IMPORT_PRE_EMPTY = "import empty\n";

    private Messager messager = null;
    private String mOptionSrc = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String index = processingEnv.getOptions().get(OPTION_FAST_DAGGER_INDEX);
        String mvpsrc = processingEnv.getOptions().get(OPTION_MVP_SRC);
        if (index == null || index.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "No option " + OPTION_FAST_DAGGER_INDEX +
                    " passed to annotation processor.Please config in app gradle file");
            return false;
        }
        if (mvpsrc == null || mvpsrc.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "No option " + OPTION_MVP_SRC +
                    " passed to annotation processor.Please config in app gradle file");
            return false;
        }
        mOptionSrc = mvpsrc;
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MvpFastDagger.class);
        for (Element element : elements){
            MvpFastDagger fastDagger = element.getAnnotation(MvpFastDagger.class);
            String name = fastDagger.name();
            if(checkNameValidate(name,element.getSimpleName().toString())){
                createIViewFile(fastDagger,index);
                createIModelFile(fastDagger,index);
                createModelImpFile(fastDagger,index);
                createPresenterFile(fastDagger,index);
//                //生成module文件
                createModuleFile(fastDagger,index);
//                //生成component文件
                generateComponentFile(fastDagger,index,element.asType().toString());
            };
        }
        return true;
    }

    //检查名字是否合法
    private boolean checkNameValidate(String name, String simpleName) {
        if(name.length() <= 0){
            log("The value of name cann't be empty.location --> " + simpleName,Diagnostic.Kind.ERROR);
            return false;
        }else {
            char ch = name.charAt(0);
            if(!Character.isLetter(ch)){
                log(simpleName + "---> The value of @MvpFastDagger's name must be start with letters range in [a - z] or [A - Z].",Diagnostic.Kind.ERROR);
                return false;
            }
        }
        return true;
    }

    //生成presenter文件
    private void createPresenterFile(MvpFastDagger fastDagger, String index) {
        //获取最后一个"."后面的内容，作为文件的名字
        int pointIndex = fastDagger.name().lastIndexOf(".");
        String fileN = fastDagger.name().substring(pointIndex + 1);
        String newName = toUpperFirstChar(fileN);
        String fileName = newName+"Presenter";
        String middleName = pointIndex == -1 ? "" : "."+fastDagger.name().substring(0,pointIndex);
        String packageName = index + ".mvp.presenter" + middleName;
        String fileFullName = packageName + "." + fileName;
        //先检查对应的presenter文件是否存在
        if(isFileExist(fileFullName)){
            return;
        }
        TypeMirror typeMirror = null;
        try{
            fastDagger.basePresenterClazz();
        }catch (MirroredTypeException ex){
            typeMirror = ex.getTypeMirror();
            log(typeMirror.toString());
        }

        String strExtend = null;
        if(typeMirror != null && !typeMirror.toString().equals(Class.class.getName())){
            strExtend = typeMirror.toString();
        }else {//将typeMirror置空
            typeMirror = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,packageName))
                .append(String.format(Locale.CHINESE,IMPORT_VIEW_FORMAT,index,middleName,newName))
                .append(String.format(Locale.CHINESE,IMPORT_MODEL_FORMAT,index,middleName,newName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"javax.inject.Inject"));
        if(strExtend != null){
            sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,strExtend));
        }

        //插入预留导入包位置
        sb.append(IMPORT_PRE_EMPTY).append("\n");
        if (fastDagger.language() == MvpFastDagger.JAVA){
            sb.append(String.format(Locale.CHINESE,CLASS_FORMAT,fileName, strExtend == null ? "" : String.format(Locale.CHINESE,"extends %s<I%sModel,I%sView>",strExtend.substring(strExtend.lastIndexOf(".")+1),newName,newName),""))
                    .append("\n")
                    .append("\t@Inject\n")
                    .append(String.format(Locale.CHINESE,"\tpublic %s(I%sModel model,I%sView view){\n",fileName,newName,newName))
                    .append(strExtend != null ? "\t\t super(model,view);\n" : "\t\t\n")
                    .append("\t}\n\n");
        }else if (fastDagger.language() == MvpFastDagger.KOTLIN){
            sb.append(
                    String.format(Locale.CHINESE,CLASS_FORMAT,fileName, strExtend == null ? "" :
                            String.format(Locale.CHINESE,"@Inject constructor(model: I%sModel,view: I%sView): %s<I%sModel,I%sView>(model,view)",newName,newName,strExtend.substring(strExtend.lastIndexOf(".")+1),newName,newName),""))
                    .append("\n");
        }
        sb.append(achieveAbstractMethod(sb,typeMirror,fastDagger.language()))
                .append("}");

        //删除预留包位置
        sb.replace(sb.indexOf(IMPORT_PRE_EMPTY),sb.indexOf(IMPORT_PRE_EMPTY)+IMPORT_PRE_EMPTY.length(),"");

        writeFileInSrc(fileFullName,sb.toString(),fastDagger.language());
    }

    //生成Iview接口
    private void createIViewFile(MvpFastDagger fastDagger, String index) {
        //获取最后一个"."后面的内容，作为文件的名字
        createInterfaceFile(fastDagger,index,"view");
    }

    //生成Imodel接口
    private void createIModelFile(MvpFastDagger fastDagger, String index) {
        //获取最后一个"."后面的内容，作为文件的名字
        createInterfaceFile(fastDagger,index,"model");
    }

    //生成Iview或imodel接口
    private void createInterfaceFile(MvpFastDagger fastDagger, String index,String type) {
        if(!type.equalsIgnoreCase("view") && !type.equalsIgnoreCase("model")){
            log("The createInterfaceFile's param type must be view or model", Diagnostic.Kind.ERROR);
        }
        type = type.toLowerCase();
        //获取最后一个"."后面的内容，作为文件的名字
        int pointIndex = fastDagger.name().lastIndexOf(".");
        String fileN = fastDagger.name().substring(pointIndex + 1);
        String newName = toUpperFirstChar(fileN);
        String fileName = "I" + newName + toUpperFirstChar(type);
        String middleName = pointIndex == -1 ? "" : "."+fastDagger.name().substring(0,pointIndex);
        String packageName = index + ".mvp."+ type.toLowerCase() + middleName;
        String fileFullName = packageName + "." + fileName;
        if(isFileExist(fileFullName)){
            return;
        }
        List<? extends TypeMirror> typeMirrors = null;
        try{
            if(type.equalsIgnoreCase("view")){
                fastDagger.iBaseViewClazz();
            }else {
                fastDagger.iBaseModelClazz();
            }
        }catch (MirroredTypesException ex){
            typeMirrors = ex.getTypeMirrors();
            log(typeMirrors.toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,packageName));
        StringBuilder sbExtend = null;
        if(typeMirrors != null && typeMirrors.size() > 0){
            sbExtend = new StringBuilder();
            if (fastDagger.language() == MvpFastDagger.KOTLIN){
                sbExtend.append(": ");
            }else {
                sbExtend.append("extends ");
            }
            int i = 0;
            for (TypeMirror typeMirror : typeMirrors){
                sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,typeMirror.toString()));
                sbExtend.append(typeMirror.toString().substring(typeMirror.toString().lastIndexOf(".")+1));
                if(i < typeMirrors.size()-1){
                    sbExtend.append(",");
                }else {
                    sb.append("\n");
                }
                i++;
            }
        }
        //插入预留导入包位置
        sb.append(IMPORT_PRE_EMPTY);

        sb.append(String.format(Locale.CHINESE,INTERFACE_FORMAT,fileName,sbExtend == null ? "" : sbExtend.toString()))
                .append("\n")
                .append("}");
        //删除预留包位置
        sb.replace(sb.indexOf(IMPORT_PRE_EMPTY),sb.indexOf(IMPORT_PRE_EMPTY)+IMPORT_PRE_EMPTY.length(),"");

        writeFileInSrc(fileFullName,sb.toString(),fastDagger.language());
    }

    //生成iModel接口实现类ModelImp类
    private void createModelImpFile(MvpFastDagger fastDagger, String index) {
        //获取最后一个"."后面的内容，作为文件的名字
        int pointIndex = fastDagger.name().lastIndexOf(".");
        String fileN = fastDagger.name().substring(pointIndex + 1);
        String newName = toUpperFirstChar(fileN);
        String fileName = newName+"ModelImp";
        String interfaceName = String.format(Locale.CHINESE,"I%sModel",newName);
        String middleName = pointIndex == -1 ? "" : "."+fastDagger.name().substring(0,pointIndex);
        String packageName = index + ".mvp.model" + middleName;
        String fileFullName = packageName + "." + fileName;
        //先检查文件是否存在
        if(isFileExist(fileFullName)){
            return;
        }
        TypeMirror typeMirror = null;
        try{
            fastDagger.baseModelImpClazz();
        }catch (MirroredTypeException ex){
            typeMirror = ex.getTypeMirror();
            log(typeMirror.toString());
        }
        String strExtend = null;
        if(typeMirror != null && !typeMirror.toString().equals(Class.class.getName())){
            strExtend = typeMirror.toString();
        }else {
            typeMirror = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,packageName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"javax.inject.Inject"))
                .append(String.format(Locale.CHINESE,IMPORT_MODEL_FORMAT,index,middleName,newName));
        if(strExtend != null){
            sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,strExtend));
        }
        //插入预留导入包位置
        sb.append(IMPORT_PRE_EMPTY).append("\n");

        if (fastDagger.language() == MvpFastDagger.KOTLIN){
            sb.append(String.format(Locale.CHINESE,CLASS_FORMAT,fileName + " @Inject constructor()",
                    (strExtend == null ? "" : String.format(Locale.CHINESE," : %s()",strExtend.substring(strExtend.lastIndexOf(".")+1))),
                    strExtend == null ? interfaceName : " , "+interfaceName))
                    .append("\n");
        }else {
            sb.append(String.format(Locale.CHINESE,CLASS_FORMAT,fileName,
                    (strExtend == null ? "" : String.format(Locale.CHINESE,"extends %s",strExtend.substring(strExtend.lastIndexOf(".")+1))),
                    " implements "+interfaceName))
                    .append("\n")
                    .append("\t@Inject\n")
                    .append(String.format(Locale.CHINESE,"\tpublic %s(){\n",fileName))
                    .append("\t}\n\n");
        }
        sb.append(achieveAbstractMethod(sb,typeMirror,fastDagger.language()))
                .append("}");
        //删除预留包位置
        sb.replace(sb.indexOf(IMPORT_PRE_EMPTY),sb.indexOf(IMPORT_PRE_EMPTY)+IMPORT_PRE_EMPTY.length(),"");

        writeFileInSrc(fileFullName,sb.toString(),fastDagger.language());
    }

    //生成Activity或Fragment类
//    private void createActivityFile(MvpFastDagger fastDagger, String index) {
////获取最后一个"."后面的内容，作为文件的名字
//        int pointIndex = fastDagger.name().lastIndexOf(".");
//        String fileN = fastDagger.name().substring(pointIndex + 1);
//        String newName = toUpperFirstChar(fileN);
//        String fileName = newName+"Activity";
//        String interfaceName = String.format(Locale.CHINESE,"I%sView",newName);
//        String presenterName = String.format(Locale.CHINESE,"%sPresenter",newName);
//        String middleName = pointIndex == -1 ? "" : "."+fastDagger.name().substring(0,pointIndex);
//        String presenterFullName = String.format(Locale.CHINESE,"%s.mvp.presenter%s.%s",index,middleName,presenterName);
//        String packageName = index + ".ui" + middleName;
//        String fileFullName = packageName + "." + fileName;
//        //先检查文件是否存在
//        if(isFileExist(fileFullName)){
//            return;
//        }
//        TypeMirror typeMirror = null;
//        String strExtend = null;
//        try{
//            fastDagger.activityExtendCls();
//        }catch (MirroredTypeException ex){
//            typeMirror = ex.getTypeMirror();
//            strExtend = typeMirror.toString();
//            log(typeMirror.toString());
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,packageName))
//                .append(String.format(Locale.CHINESE,IMPORT_VIEW_FORMAT,index,middleName,newName))
//                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,presenterFullName))
//                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,typeMirror.toString())).append("\n");
//        sb.append(String.format(Locale.CHINESE,CLASS_FORMAT,fileName,
//                (strExtend == null ? "" : String.format(Locale.CHINESE,"extends %s<%s>",strExtend.substring(strExtend.lastIndexOf(".")+1),presenterName)),
//                " implements "+interfaceName))
//                .append("\n")
//                .append("}");
//
//        writeFileInSrc(fileFullName,sb.toString());
//    }

    /**
     * 生成module文件
     * @param fastDagger
     * @param index
     * @return
     */
    private void createModuleFile(MvpFastDagger fastDagger,String index) {
        StringBuilder sb = new StringBuilder();
        //获取IxxMode和IxxView的简单类名
        String modelValue = index + ".mvp.model." + fastDagger.name();
        String viewValue = index + ".mvp.view." + fastDagger.name();
        String moduleValue = index + ".di.module." + fastDagger.name();
        int _i = fastDagger.name().lastIndexOf(".");
        String pbName = _i == -1 ? fastDagger.name() : fastDagger.name().substring(_i + 1);
        modelValue = modelValue.replace(pbName,"");
        viewValue = viewValue.replace(pbName,"");
        moduleValue = moduleValue.replace(pbName,"");
        pbName = toUpperFirstChar(pbName);
        String simpleModelName = "I"+pbName+"Model";
        String simpleViewName = "I"+pbName+"View";
        String simpleModelImpName = pbName+"ModelImp";
        String moduleName = pbName+"Module";
        String moduleFileName = String.format(Locale.CHINESE,"%s%s",moduleValue,moduleName);
        //先检查文件是否存在
        if(isFileExist(moduleFileName)){
            return;
        }
        TypeMirror scopeValue = null;
        try {
            fastDagger.scopeClazz();
        }catch( MirroredTypeException mte ) {
            scopeValue = mte.getTypeMirror();
        }
        String simpleScopeName = scopeValue.toString().substring(scopeValue.toString().lastIndexOf(".")+1);
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,moduleValue.substring(0,moduleValue.lastIndexOf("."))))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,modelValue + simpleModelName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,modelValue + simpleModelImpName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,viewValue + simpleViewName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Module"))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Provides"))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,scopeValue.toString()))
                .append("\n")
                .append("@Module\n")
                .append(String.format(Locale.CHINESE,CLASS_FORMAT,moduleName,"",""))
                .append(String.format(Locale.CHINESE,"\n\tprivate %s view;\n\n",simpleViewName))
                .append(String.format(Locale.CHINESE,"\tpublic %s(%s view) {\n\t\tthis.view = view;\n\t}\n\n",moduleName,simpleViewName))
                .append(String.format(Locale.CHINESE,"\t@%s\n\t@Provides\n\t%s provide%s(){\n\t\treturn this.view;\n\t}\n\n",
                        simpleScopeName,
                        simpleViewName,
                        simpleViewName.replace("I","")))
                .append(String.format(Locale.CHINESE,"\t@%s\n\t@Provides\n\t%s provide%s(%s model){\n\t\treturn model;\n\t}\n\n",
                        simpleScopeName,
                        simpleModelName,
                        simpleModelName.replace("I",""),
                        simpleModelImpName))
                .append("}");

        log("--->>>"+moduleValue+moduleName);
        writeFileInSrc(moduleFileName,sb.toString());
    }

    /**
     * 生成Component类文件
     * @param fastDagger
     * @param packageName
     * @param actClazz
     */
    private void generateComponentFile(MvpFastDagger fastDagger, String packageName, String actClazz) {
        int _i = fastDagger.name().lastIndexOf(".");
        String pbName = _i == -1 ? fastDagger.name() : fastDagger.name().substring(_i + 1);
        String moduleValue = packageName + ".di.module." + fastDagger.name();
        String componentValue = packageName + ".di.component."+fastDagger.name();
        moduleValue = moduleValue.replace(pbName,"");
        componentValue = componentValue.replace(pbName,"");
        //将首字母大写用于拼接成对应的类名
        pbName = toUpperFirstChar(pbName);
        //module class' simple name
        String moduleSimpleName = pbName + "Module";
        String componentSimpleName = pbName + "Component";
        //要生成的component类的全路径名称
        String componentFullName = String.format("%s%s",componentValue,componentSimpleName);
        //先检查文件是否存在
        if(isFileExist(componentFullName)){
            return;
        }
        TypeMirror scopeValue = null;
        try {
            fastDagger.scopeClazz();
        }catch( MirroredTypeException mte ) {
            scopeValue = mte.getTypeMirror();
        }
        //activity's or fragment's simple class name
        String actSimpleName = actClazz.substring(actClazz.lastIndexOf(".")+1);
        String scopeFullName = scopeValue.toString();
        String scopeSimpleName = scopeFullName.substring(scopeFullName.lastIndexOf(".")+1);

        //获取到的依赖component
        List<? extends TypeMirror> dependenceList = null;
        try {
            fastDagger.dependencies();
        }catch (MirroredTypesException e){
            dependenceList = e.getTypeMirrors();
        }

        List<? extends TypeMirror> moduleList = null;
        try {
            fastDagger.modules();
        }catch (MirroredTypesException e){
            moduleList = e.getTypeMirrors();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,PACKAGE_FORMAT,componentFullName.substring(0,componentFullName.lastIndexOf("."))))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,moduleValue + moduleSimpleName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,actClazz))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,scopeFullName))
                .append(String.format(Locale.CHINESE,IMPORT_FORMAT,"dagger.Component"));
        if(dependenceList != null){
            for (TypeMirror mirror : dependenceList){
                sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,mirror.toString()));
            }
        }
        if(moduleList != null){
            for (TypeMirror mirror : moduleList){
                sb.append(String.format(Locale.CHINESE,IMPORT_FORMAT,mirror.toString()));
            }
        }
        sb.append("\n")
                .append(String.format(Locale.CHINESE,"@%s\n",scopeSimpleName))
                .append(String.format(Locale.CHINESE,"@Component(%s%s)\n",
                        changeDependenciesToStr(moduleList,"modules",moduleSimpleName),
                        changeDependenciesToStr(dependenceList,"dependencies",null)))
                .append(String.format(Locale.CHINESE,INTERFACE_FORMAT,componentSimpleName,""))
                .append(String.format(Locale.CHINESE,"\tvoid inject(%s %s);\n",actSimpleName,changeClassNameToVar(actSimpleName)))
                .append("}");

        writeFileInSrc(componentFullName,sb.toString());
    }

    /**
     * 根据列表获取依赖的Component类
     * @param dependenceList
     * @return
     */
    private String changeDependenciesToStr(List<? extends TypeMirror> dependenceList,String typeName,String defaultClazz) {
        if(dependenceList == null || dependenceList.size() <= 0){
            return typeName.equalsIgnoreCase("modules") ? String.format(Locale.CHINESE,"%s=%s.class",typeName,defaultClazz) : "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINESE,"%s%s = {",typeName.equalsIgnoreCase("modules")?"":",",typeName));
        if(defaultClazz != null && defaultClazz.length() > 0){
            sb.append(String.format(Locale.CHINESE,"%s.class,",defaultClazz));
        }
        int i = 0;
        for (TypeMirror mirror : dependenceList){
            sb.append(String.format(Locale.CHINESE,"%s.class",mirror.toString().substring(mirror.toString().lastIndexOf(".")+1)));
            if(i < dependenceList.size()-1){
                sb.append(",");
            }
            i++;
        }
        sb.append("}");

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

    private String toUpperFirstChar(String name){
        if(name.length() == 1){
            return name.toUpperCase();
        }
        return String.format(Locale.CHINESE,"%s%s",name.substring(0,1).toUpperCase(),name.substring(1));
    }

    /**
     * 检查要生成的文件是否存在，如果存在了就不去生成了，因为生成后会进行复制覆盖原来的文件，故需要检测文件是否存在
     * @param filePath
     * @return
     */
    private boolean isFileExist(String filePath){
        filePath = filePath.replace(".","/") + ".java";
        filePath = mOptionSrc + "/" + filePath;
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 在src目录下生成java文件
     * @param fileName
     * @param content
     */
    private void writeFileInSrc(String fileName,String content){
        writeFileInSrc(fileName, content,MvpFastDagger.JAVA);
    }

    /**
     * 在src目录下生成java文件
     * @param fileName
     * @param content
     */
    private void writeFileInSrc(String fileName,String content,int language){
        String filePath = fileName.replace(".","/") + ".java";
        //kotlin时
        if (language == MvpFastDagger.KOTLIN){
            content = content.replace(";","").replace("public ","").replace("java.lang.String","kotlin.String");
            filePath = filePath.replace(".java",".kt");
        }
        filePath = mOptionSrc + "/" + filePath;
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log("生成文件失败--> "+ filePath + ",原因 -> " + e.getMessage(),Diagnostic.Kind.ERROR);
        }
    }

    /**
     * 将内容写到文件中
     * @param fileName
     * @param content
     */
    private void writeFile(String fileName,String content){
        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(fileName);
            Writer writer = fileObject.openWriter();
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log("创建文件:"+fileName+" ----> "+e.getMessage());
        }
    }

    /**
     * 打印日志
     * @param msg
     */
    private void log(String msg, Diagnostic.Kind... kind){
        if(messager == null){
            messager = processingEnv.getMessager();
        }
        messager.printMessage(kind == null || kind.length <= 0 ? Diagnostic.Kind.NOTE : kind[0],msg);
    }

    /**
     * 检查父类中的抽象方法，并实现
     *
     * 通过反射拿到抽象方法，并实现
     *
     * @param typeMirror
     * @return
     */
    private String achieveAbstractMethod(StringBuilder parent,TypeMirror typeMirror,int language){
        //typeMirror是空 或者 没有父类 就不用检查是否有抽象方法了
        log("typeMirror -- > " + typeMirror);
        if(typeMirror == null || typeMirror.getClass() == null){
            return "";
        }
        TypeElement clzzTypeElement = (TypeElement)processingEnv.getTypeUtils().asElement(typeMirror);
        Set<? extends Modifier> clzzModifiers = clzzTypeElement.getModifiers();
        boolean isAbsClzz = false;
        for (Modifier modifier : clzzModifiers){
            if (modifier == Modifier.ABSTRACT){
                isAbsClzz = true;
                break;
            }
        }
        //非抽象类 则直接返回空字符串，因为不会有抽象方法
        if(!isAbsClzz){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder importSb = new StringBuilder();
        List<? extends Element> innerElements = clzzTypeElement.getEnclosedElements();//获取该类的内层元素
        for (Element element : innerElements){
            ElementKind elementKind = element.getKind();
            if(elementKind == ElementKind.METHOD){
                //查找类的抽象方法
                boolean isAbsMethod = false;
                Set<? extends Modifier> methodModifier = element.getModifiers();
                if(methodModifier != null && methodModifier.size() > 0){
                    for (Modifier m : methodModifier){
                        if(m == Modifier.ABSTRACT){
                            isAbsMethod = true;
                        }
                    }
                }
                //如果是抽象方法则进行实现该方法
                if(isAbsMethod){
                    sb.append("\n\t");
                    if (language == MvpFastDagger.KOTLIN){
                        sb.append("override ");
                    }
                    for (Modifier m : methodModifier){
                        if(m != Modifier.ABSTRACT){
                            if (language != MvpFastDagger.KOTLIN || m != Modifier.PUBLIC){
                                sb.append(m.name().toLowerCase()).append(" ");
                            }
                        }
                    }
                    ExecutableElement methodElement = (ExecutableElement)element;
                    TypeMirror returnType = methodElement.getReturnType();
                    TypeKind kind = returnType.getKind();
                    if (language == MvpFastDagger.KOTLIN){
                        String returnName = kind.isPrimitive()|| kind == TypeKind.VOID ? convertKotlinBaseType(kind): (insertImportClazz(parent,importSb,returnType.toString())+"?");
                        sb.append(" fun ")
                                .append(methodElement.getSimpleName())
                                .append("(");
                        //参数设置
                        List<? extends VariableElement> parameters = methodElement.getParameters();
                        int v = 0;
                        for (VariableElement variableElement : parameters){
                            TypeKind tk = variableElement.asType().getKind();
                            sb.append(variableElement.getSimpleName())
                                    .append(" : ")
                                    .append(tk.isPrimitive() || kind == TypeKind.VOID?convertKotlinBaseType(tk) : insertImportClazz(parent,importSb,variableElement.asType().toString()));
                            if(v < parameters.size()-1){
                                sb.append(",");
                            }
                            v++;
                        }

                        sb.append(String.format("):%s{\n",returnName.isEmpty()?"Unit":returnName));
                    }else {
                        sb.append(kind.isPrimitive() ? returnType.toString() : insertImportClazz(parent,importSb,returnType.toString()))
                                .append(" ")
                                .append(methodElement.getSimpleName())
                                .append("(");
                        List<? extends VariableElement> parameters = methodElement.getParameters();
                        int v = 0;
                        for (VariableElement variableElement : parameters){
                            sb.append(variableElement.asType().getKind().isPrimitive()?variableElement.asType().toString() : insertImportClazz(parent,importSb,variableElement.asType().toString()))
                                    .append(" ")
                                    .append(variableElement.getSimpleName());
                            if(v < parameters.size()-1){
                                sb.append(",");
                            }
                            v++;
                        }
                        sb.append("){\n");
                    }
                    if(kind != TypeKind.VOID){
                        String value = null;
                        if(kind.isPrimitive()){
                            if(kind == TypeKind.BOOLEAN){
                                value = "false";
                            }else if(kind == TypeKind.DOUBLE || kind == TypeKind.FLOAT){
                                value = "0f";
                                if (kind == TypeKind.DOUBLE && language == MvpFastDagger.KOTLIN){
                                    value = "0.0";
                                }
                            }else if(kind == TypeKind.LONG){
                                value = "0L";
                            }else {
                                value = "0";
                            }
                        }else {
                            value = "null";
                        }
                        sb.append(String.format(Locale.CHINESE,"\t\treturn %s;\n",value));
                    }else {
                        sb.append("\t\t\n");
                    }
                    sb.append("\t}\n");
                }
            }
        }
        parent.insert(parent.indexOf(IMPORT_PRE_EMPTY),importSb.toString());
        return sb.toString();
    }

    //引入对应的包,并返回类的简单名称
    private String insertImportClazz(StringBuilder parent,StringBuilder importSb,String clazzName){
        if(importSb == null){
            importSb = new StringBuilder();
        }
        String str = String.format(Locale.CHINESE,IMPORT_FORMAT,clazzName);
        if(parent.indexOf(str) == -1 && importSb.indexOf(str) == -1){
            importSb.append(str);
        }
        return clazzName.substring(clazzName.lastIndexOf(".")+1);
    }


    /**
     * 将java类型转换成Kotlin基本类型
     * @param kind
     * @return
     */
    private String convertKotlinBaseType(TypeKind kind){
        String strReturnName = "";
        if (kind != TypeKind.VOID){
            if(kind.isPrimitive()){
                if(kind == TypeKind.BOOLEAN){
                    strReturnName = "Boolean";
                }else if(kind == TypeKind.DOUBLE ){
                    strReturnName = "Double";
                }else if (kind == TypeKind.FLOAT){
                    strReturnName = "Float";
                }else if(kind == TypeKind.LONG){
                    strReturnName = "Long";
                }else if (kind == TypeKind.INT){
                    strReturnName = "Int";
                }else if (kind == TypeKind.BYTE){
                    strReturnName = "Byte";
                }else if (kind == TypeKind.SHORT){
                    strReturnName = "Short";
                }else if (kind == TypeKind.CHAR){
                    strReturnName = "Char";
                }
            }
        }else {
            strReturnName = "Unit";
        }
        return strReturnName;
    }
}
