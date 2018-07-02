package app

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import de.kaesdingeling.hybridmenu.HybridMenu
import de.kaesdingeling.hybridmenu.components.HMButton
import de.kaesdingeling.hybridmenu.components.HMLabel
import de.kaesdingeling.hybridmenu.components.HMTextField
import de.kaesdingeling.hybridmenu.data.MenuConfig
import de.kaesdingeling.hybridmenu.design.DesignItem
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
class MainLayout extends HybridMenu {
    @Override
    boolean init(VaadinSession vaadinSession, UI ui) {
        withConfig(MenuConfig.get()
                .withDesignItem(DesignItem.whiteDesign))
        topMenu.tap {
            add(HMTextField.get(VaadinIcon.SEARCH, "Search"))
            add(HMButton.get()
                    .withIcon(VaadinIcon.HOME)
                    .withDescription("Home")
                    .withNavigateTo(HomePage))
            add(HMButton.get()
                    .withIcon(VaadinIcon.COG_O)
                    .withDescription("Light theme")
                    .withClickListener { switchTheme(DesignItem.whiteDesign) })
            add(HMButton.get()
                    .withIcon(VaadinIcon.COG)
                    .withDescription("Dark theme")
                    .withClickListener { switchTheme(DesignItem.darkDesign) })
            add(HMButton.get()
                    .withIcon(VaadinIcon.RESIZE_H)
                    .withDescription("Minimize")
                    .withClickListener { leftMenu.toggleSize() })
        }
        leftMenu.tap {
            add(HMLabel.get()
                    .withCaption("<b>Dapper</b>Corp"))
        }
        breadCrumbs.setRoot(
                leftMenu.add(HMButton.get()
                        .withCaption("Home")
                        .withIcon(VaadinIcon.HOME)
                        .withNavigateTo(HomePage))
        )
        leftMenu.tap {
            add(HMButton.get()
                    .withCaption("Settings")
                    .withIcon(VaadinIcon.COGS)
                    .withNavigateTo(SettingsPage))
        }
        return true
    }
}

@Route(value = "", layout = MainLayout)
class HomePage extends VerticalLayout {
    HomePage() {
        add(new H1("Home"), new Text("Lorem Ipsum"))
    }
}

@Route(value = "settings", layout = MainLayout)
class SettingsPage extends VerticalLayout {
    SettingsPage() {
        add(new H1("Settings"), new Text("Lorem Ipsum"))
    }
}
