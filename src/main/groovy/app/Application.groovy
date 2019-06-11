package app

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext

import javax.annotation.PostConstruct
import java.lang.annotation.*
import java.util.function.Consumer
import java.util.function.Function

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        new SpringApplication(Application).tap {
            // further config...
            bannerMode = Banner.Mode.OFF
            run(args)
        }
    }
}

// Annotations

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Spot {
    int order() default 0;

    // DECIDE: class based?  name based?
    Class<? extends SpotContainer> container();

    Class<? extends SpotComponentFactory> componentFactory();
}

interface SpotContainer extends Consumer<Component> {}

interface SpotComponentFactory extends Function<Object, Component> {}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Translatable {
    String i18nKey();

    String i18nDefault() default ""; // FIXME: can not set default null?

    // TODO: can args even be handled here?
}

// Spot Containers

@SpringComponent
@UIScope
class MainMenu extends Div implements SpotContainer {

    private final ApplicationContext ctx

    @Autowired
    MainMenu(ApplicationContext ctx) {
        this.ctx = ctx
        element.themeList.add('dark')
    }

    @PostConstruct
    void init() {
        // FIXME: move into tooling
        ctx.getBeansWithAnnotation(Spot).collect { k, v ->
            [v.getClass().getAnnotation(Spot), v]
        }.findAll { i, v ->
            i.container() == MainMenu
        }.sort { // can't destructure here
            it[0].order()
        }.each { i, v ->
            accept(ctx.getBean(i.componentFactory()).apply(v))
        }
    }

    @Override
    void accept(Component component) {
        add(new Div(component))
    }
}

// Spot Component Factories

@SpringComponent
class RouterLinkSpotComponentFactory implements SpotComponentFactory {

    private final TranslationService translationService
    @Autowired RouterLinkSpotComponentFactory(TranslationService translationService) { this.translationService = translationService }

    @Override
    Component apply(Object o) {
        return new RouterLink(translationService.t(o.getClass().getAnnotation(Translatable)), o.getClass())
    }
}

@SpringComponent
class ButtonSpotComponentFactory implements SpotComponentFactory {

    private final TranslationService translationService
    @Autowired
    ButtonSpotComponentFactory(TranslationService translationService) { this.translationService = translationService }

    @Override
    Component apply(Object o) {
        new Button(translationService.t(o.getClass().getAnnotation(Translatable)), { (o as Runnable).run() })
    }
}

@SpringComponent
class TranslationService {
    String t(Translatable t) {
        t?.i18nDefault() ?: "T: ${t?.i18nKey()}"
    }
}

// EXAMPLES

@SpringComponent
@UIScope
@Theme(Material)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends HorizontalLayout implements RouterLayout {

    private MainMenu mainMenu

    @Autowired
    MainLayout(MainMenu mainMenu) {
        this.mainMenu = mainMenu
    }

    @PostConstruct
    void init() {
        add(mainMenu)
    }
}

@Route(value = '', layout = MainLayout)
@SpringComponent
@UIScope
@Spot(container = MainMenu, componentFactory = RouterLinkSpotComponentFactory)
@Translatable(i18nKey = 'i18n.home', i18nDefault = 'Home')
class HomeView extends Div {
    @PostConstruct
    void init() {
        add(new H1("Home"), new Div(new Span("This is a test view")))
    }
}

@Route(value = 'x1', layout = MainLayout)
@SpringComponent
@UIScope
@Spot(container = MainMenu, componentFactory = RouterLinkSpotComponentFactory)
@Translatable(i18nKey = 'i18n.x1', i18nDefault = 'X1')
class X1View extends Div {
    @PostConstruct
    void init() {
        add(new H1("X1"), new Div(new Span("This is a test view")))
    }
}

@Route(value = 'x2', layout = MainLayout)
@SpringComponent
@UIScope
@Spot(container = MainMenu, componentFactory = RouterLinkSpotComponentFactory)
@Translatable(i18nKey = 'i28n.x2', i18nDefault = 'X2')
class X2View extends Div {
    @PostConstruct
    void init() {
        add(new H1("X2"), new Div(new Span("This is a test view")))
    }
}

@SpringComponent
@UIScope
@Spot(container = MainMenu, componentFactory = ButtonSpotComponentFactory, order = 1000)
@Translatable(i18nKey = 'i28n.doc', i18nDefault = 'Doc')
class DocCommand implements Runnable {
    @Override
    void run() {
        Notification.show("Doc")
    }
}
