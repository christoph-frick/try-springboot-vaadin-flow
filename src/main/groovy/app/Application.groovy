package app

import com.github.appreciated.app.layout.annotations.MenuCaption
import com.github.appreciated.app.layout.annotations.MenuIcon
import com.github.appreciated.app.layout.behaviour.AppLayout
import com.github.appreciated.app.layout.behaviour.Behaviour
import com.github.appreciated.app.layout.builder.AppLayoutBuilder
import com.github.appreciated.app.layout.builder.interfaces.NavigationElementContainer
import com.github.appreciated.app.layout.component.appmenu.MenuHeaderComponent
import com.github.appreciated.app.layout.component.appmenu.left.LeftClickableComponent
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder
import com.github.appreciated.app.layout.component.appmenu.top.TopNavigationComponent
import com.github.appreciated.app.layout.design.AppLayoutDesign
import com.github.appreciated.app.layout.entity.Section
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import groovy.util.logging.Slf4j
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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

@RestController
class HelloWorldController {
    @GetMapping("/hello")
    String index() {
        return "Hello World"
    }
}

@Slf4j
@Theme(Material)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends AppLayoutRouterLayout {
    @Override
    AppLayout getAppLayout() {
        def leftAppMenu = LeftAppMenuBuilder.get()
                .addToSection(new MenuHeaderComponent("The Header Title", "The Header Subtitle", "/frontend/images/does/not/exist.svg"), Section.HEADER)
                .add(new LeftNavigationComponent("Home", VaadinIcon.HOME.create(), MainLayout))
                .build()
        return AppLayoutBuilder.get(Behaviour.LEFT_RESPONSIVE_HYBRID)
                .withTitle("Test with AppLayout")
                .withDesign(AppLayoutDesign.MATERIAL)
                .withAppMenu(leftAppMenu)
                .build()
    }
}

@Route(value = '', layout = MainLayout)
@Slf4j
@MenuCaption("Home")
@MenuIcon(VaadinIcon.HOME)
class MainView extends Composite<Div> {
    MainView() {
        H1 label
        TextField input
        content.add(
                label = new H1(),
                input = new TextField().tap {
                    addValueChangeListener({
                        label.text = "Hello ${it.value ?: "World"}"
                    } as HasValue.ValueChangeListener)
                    valueChangeMode = ValueChangeMode.EAGER
                    placeholder = "Who to greet?"
                    value = "Flow"
                    focus()
                },
                new Button("Greet the server log", {
                    log.info "Hello ${input.value ?: "World"} to the server log"
                }),
        )
    }
}
