package app

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.OptionalParameter
import com.vaadin.flow.router.ParentLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        // further config...
        app.run(args)
    }
}

@Slf4j
@Theme(Lumo)
@HtmlImport('frontend://styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Div implements RouterLayout {
    MainLayout() {
        setSizeFull()
    }
}

@ParentLayout(MainLayout)
class MenuLayout extends HorizontalLayout implements RouterLayout {
    VerticalLayout menu
    MenuLayout() {
        setSizeFull()
        add(
            new Div(menu = new VerticalLayout()).tap{
                setWidth("20em")
                addClassName('menu')
                element.setAttribute('theme', 'dark')
            }
        )
        menu.add(new H1("My App").tap{
            addClassName('menu__title')
        })
        addMenuItem(VaadinIcon.DASHBOARD, "Dashboard", DashboardView)
        addMenuItem(VaadinIcon.ABACUS, "Hello", HelloView)
    }
    def addMenuItem(VaadinIcon icon ,String label, Class<? extends Component> viewClass) {
        menu.add(new RouterLink(null, viewClass).tap{
            addClassName('menu__navitem')
            add(
                    new Icon(icon),
                    new Text(label),
            )
        })
    }
}

class HelloRequest {
    String text
}

@Route(value="", layout = MenuLayout)
class DashboardView extends Div {
    DashboardView() {
        setSizeFull()
        def input = new TextField("Say hello to")
        def binder = new Binder<HelloRequest>(HelloRequest)
        binder
            .forField(input)
            .bind("text")
        binder.bean = new HelloRequest()
        def button = new Button("Go", {
            getUI().ifPresent{ it.navigate("hello/${binder.bean.text}") }
        }).tap{
            element.themeList.add('primary')
        }
        add(
                new FormLayout(
                        input,
                        button
                )
        )
    }
}

@Route(value="hello", layout = MenuLayout)
class HelloView extends Div implements HasUrlParameter<String> {
    HelloView() {
        setSizeFull()
        text = "Dashboard"
    }
    @Override
    void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        setText("Hello, ${parameter?:"World"}!")
    }
}
