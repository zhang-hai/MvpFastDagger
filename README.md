# MvpFastDagger
MVP+Dagger+Annotation项目架构快速生成mvp相应的文件。

MVP+Dagger+Annotation架构中每新增一个Activity或Fragment都需要新增XXPresenter、IXXview、IXXModel、XXModleImp、XXModule、XXComponent类，手动增加比较繁琐，该库可以帮助我们快速生成mvp相应的文件。

> 注:本开源库适用于MVP+Dagger+Annotation架构的项目。在项目中需要引入Dagger和Annotation这两个开源库。

### MvpFastDagger使用仅需一步 ###

使用该注解时，需要先创建Activity类，然后使用@MvpFastDagger，***已支持生成Kotlin语言***。

注解使用方式：
    
	/**
	* name 对应要创建的类的名字
	* basePresenterClazz是需要创建的presenter类的父类，
	* iBaseViewClazz是创建view类的父类，
	* iBaseModelClazz是创建model接口类的父类，
	* baseModelImpClazz是要生成的ModelImp的父类，
	* scopeClazz对应的Imodel Iview的生命周期，
	* modules对应Dagger中@Component注解中的modules字段，
	* dependencies是对应@Component注解中的dependencies字段，该字段为可选填字段
	* language 对应要生成的类使用的语言，支持JAVA,KOTLIN，默认Java
	*/
	@MvpFastDagger(name = "login",
        basePresenterClazz = BasePresenter.class,
        iBaseViewClazz = IViewAdvance.class,
        iBaseModelClazz = IModel.class,
        baseModelImpClazz = BaseModel.class,
        scopeClazz = PerActivity.class,
        modules = AppModule.class,
        dependencies = AppComponent.class
        language = MvpFastDagger.KOTLIN)
	public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {
		
		...

	}

`` 其中name可以直接写名字，如："login",可以用"."进行分割，如：“login.login”,这时会生成login目录，该目录下再生成放对应的java文件。 ``

### 将MvpFastDagger引入到你的项目中 ###

在Gradle中配置设置：

1.在根目录的build.gradle文件中配置如下：
	
	allprojects {
    	repositories {
    	    ...
    	    
    	    maven { url "https://jitpack.io" }  //增加该配置，使编译时能从jitpack库中找资源
    	}
	}

2.在app项目的build.gradle文件中增加引用：

	android {
    	...
		defaultConfig {
        	javaCompileOptions{
        	    annotationProcessorOptions{
					arguments = ["fastDaggerIndex": "org.harry.fastdagger.demo",//这里配置你的包名，生成的文件会在这个目录下面
                             "mvpSrcDir" : file("src/main/java").getAbsolutePath()]//主工程src路径，一般情况下该值不用修改
        	    }
        	}
    	}
	}

	dependencies {
		...
		
		implementation 'com.github.zhang-hai.MvpFastDagger:fastdagger:1.2.1'		//使用mvpfastdagger库
    	annotationProcessor 'com.github.zhang-hai.MvpFastDagger:fastdagger:1.2.1'	//使用mvpfastdagger库中的注解处理器
		
	}


### 版本更新记录 ###
***V1.2.1***
- 1.修复再次编译时，会删除已生成的kotlin文件

***V1.2.0***

- 1.升级支持AndroidX，升级插件的gradle版本；
- 2.新增`language`字段，支持生成Kotlin语言；
