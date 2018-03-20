package app

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
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
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Div implements RouterLayout {
}

@ParentLayout(MainLayout)
class MenuLayout extends Div implements RouterLayout {
    MenuLayout() {
        addClassName('menu')
        addMenuItem("Dashboard", DashboardView)
        addMenuItem("Hello", HelloView)
    }
    def addMenuItem(String label, Class<? extends Component> viewClass) {
        add(new RouterLink(label, viewClass))
    }
}

class HelloRequest {
    String text
}

@Route(value="", layout = MenuLayout)
class DashboardView extends Div {
    DashboardView() {
        def input = new TextField("Say hello to")
        def binder = new Binder<HelloRequest>(HelloRequest)
        binder
            .forField(input)
            .bind("text")
        binder.bean = new HelloRequest()
        def button = new Button("Go", {
            getUI().ifPresent{ it.navigate("hello/${binder.bean.text}") }
        })
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
        text = "Dashboard"
    }
    @Override
    void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        setText("Hello, ${parameter?:"World"}!")
    }
}
