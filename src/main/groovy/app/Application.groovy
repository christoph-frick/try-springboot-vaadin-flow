package app

import com.vaadin.router.Route
import com.vaadin.ui.Composite
import com.vaadin.ui.button.Button
import com.vaadin.ui.common.HtmlImport
import com.vaadin.ui.html.Div
import com.vaadin.ui.html.H1
import com.vaadin.ui.textfield.TextField
import groovy.util.logging.Slf4j
import org.apache.coyote.http2.Http2Protocol
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        // further config...
        app.run(args)
    }

    // enable http2
    @Bean
    EmbeddedServletContainerCustomizer tomcatCustomizer() {
        { container ->
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                (container as TomcatEmbeddedServletContainerFactory).addConnectorCustomizers({ connector ->
                    connector.addUpgradeProtocol(new Http2Protocol())
                } as TomcatConnectorCustomizer)
            }
        }
    }
}

@Route('')
@Slf4j
@HtmlImport('frontend:///styles.html')
class MainLayout extends Composite<Div> {
    MainLayout() {
        H1 label
        TextField input
        content.add(
                label = new H1(),
                input = new TextField().with {
                    addValueChangeListener {
                        label.text = "Hello ${it.value ?: "World"}"
                    }
                    value = "Flow"
                    focus()
                    it
                },
                new Button("Greet the server log", {
                    log.info "Hello ${input.value ?: "World"} to the server log"
                }),
        )
    }
}

