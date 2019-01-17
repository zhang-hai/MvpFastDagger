# MvpFastDagger
MVP+Dagger+Annotation项目架构快速生成mvp相应的文件。

MVP+Dagger+Annotation架构中每新增一个Activity或Fragment都需要新增XXPresenter、IXXview、IXXModel、XXModleImp、XXModule、XXComponent类，手动增加比较繁琐，该库可以帮助我们快速生成mvp相应的文件。

``注:本开源库适用于MVP+Dagger+Annotation架构的项目。``

### MvpFastDagger使用仅需一步 ###

使用该注解时，需要先创建Activity类，然后使用@MvpFastDagger。

注解使用方式：
    
	/**
	* name 对应要创建的类的名字，如：IxxxPresenter,IxxxView,IxxxModel,xxxModelImp,xxxModule,xxxComponent,
	* basePresenterClazz是需要创建的presenter类的父类，
	* iBaseViewClazz是创建view类的父类，
	* iBaseModelClazz是创建model接口类的父类，
	* baseModelImpClazz是要生成的ModelImp的父类，
	* scopeClazz对应的Imodel Iview的生命周期，
	* modules对应Dagger中@Component注解中的modules字段，
	* dependencies是对应@Component注解中的dependencies字段，该字段为可选填字段
	*/
	@MvpFastDagger(name = "login",
        basePresenterClazz = BasePresenter.class,
        iBaseViewClazz = IViewAdvance.class,
        iBaseModelClazz = IModel.class,
        baseModelImpClazz = BaseModel.class,
        scopeClazz = PerActivity.class,
        modules = AppModule.class,
        dependencies = AppComponent.class)
	public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {
		
		...

	}



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
		
		implementation 'com.github.zhang-hai:mvpfastdagger:version'		//使用mvpfastdagger库
    	annotationProcessor 'com.github.zhang-hai:mvpfastdagger:version'	//使用mvpfastdagger库中的注解处理器
		
	}


