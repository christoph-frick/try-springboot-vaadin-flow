package app

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.polymertemplate.Id
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.*
import com.vaadin.flow.templatemodel.TemplateModel
import com.vaadin.flow.theme.AbstractTheme
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

@HtmlImport("frontend://bower_components/vaadin-material-theme/color.html")
@HtmlImport("frontend://bower_components/vaadin-material-theme/typography.html")
@HtmlImport("frontend://bower_components/vaadin-material-theme/font-icons.html")
class Material implements AbstractTheme {

    @Override
    String getBaseUrl() {
        return "src/"
    }

    @Override
    String getThemeUrl() {
        return "theme/material/"
    }

    @Override
    List<String> getHeaderInlineContents() {
        ["""
            <custom-style>
             <style include="material-color material-typography"></style>
            </custom-style>
        """]
    }
}

@Slf4j
@Theme(Material)
@HtmlImport('frontend://styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Div implements RouterLayout {
}

@ParentLayout(MainLayout)
@Tag('my-app')
@HtmlImport('frontend://layout.html')
class MenuLayout extends PolymerTemplate<TemplateModel> implements RouterLayout {

    @Id("title")
    Div titleDiv

    @Id("menu")
    Div menu

    MenuLayout() {
        addMenuItem("Dashboard", DashboardView)
        addMenuItem("Hello", HelloView)
        title = "My App"
    }

    def addMenuItem(String label, Class<? extends Component> viewClass) {
        menu.add(new RouterLink(label, viewClass))
    }

    void setTitle(String title) {
        this.titleDiv.text = title
    }

}

class HelloRequest {
    String text
}

@Route(value = "dashboard", layout = MenuLayout)
class DashboardView extends Div {
    DashboardView() {
        def input = new TextField("Say hello to")
        def binder = new Binder<HelloRequest>(HelloRequest)
        binder
                .forField(input)
                .bind("text")
        binder.bean = new HelloRequest()
        def button = new Button("Go", {
            getUI().ifPresent { it.navigate("hello/${binder.bean.text}") }
        })
        add(
                new FormLayout(
                        input,
                        button
                )
        )
    }
}

@Route(value = "hello", layout = MenuLayout)
class HelloView extends Div implements HasUrlParameter<String> {
    HelloView() {
        text = "Dashboard"
    }

    @Override
    void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        setText("Hello, ${parameter ?: "World"}!")
    }
}
