package app

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.function.SerializableFunction
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.material.Material
import groovy.transform.Immutable
import groovy.util.logging.Slf4j
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

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

enum State {

    YES(VaadinIcon.PLUS_CIRCLE_O), NO(VaadinIcon.MINUS_CIRCLE_O), MAYBE(VaadinIcon.QUESTION_CIRCLE_O)

    VaadinIcon icon

    State(VaadinIcon icon) {
        this.icon = icon
    }
}

@Immutable
class Bean {
    String name
    State state
}

@Route('')
@Slf4j
@Theme(Material)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Composite<VerticalLayout> {
    MainLayout() {
        content.add(new Grid<Bean>(Bean).tap {
            removeAllColumns()
            addColumn('name')
            addColumn(new ComponentRenderer({ new Icon(it.state.icon) } as SerializableFunction)).setHeader("State")
            setItems(
                    new Bean("Foo", State.YES),
                    new Bean("Bar", State.NO),
                    new Bean("Qux", State.MAYBE),
            )
        })
    }
}

