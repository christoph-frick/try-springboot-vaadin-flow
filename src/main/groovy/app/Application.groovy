package app

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.router.*
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import groovy.util.logging.Slf4j
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

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

@Route('')
@Slf4j
@Theme(Material)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Composite<VerticalLayout> {
    MainLayout() {
        content.add(
                new H1("Works")
        )
    }
}

@Route('error')
class ErrorLayout extends RouteNotFoundError {
    @Override
    int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        element.setText("This target does not exist")
        return HttpServletResponse.SC_NOT_FOUND
    }
}
