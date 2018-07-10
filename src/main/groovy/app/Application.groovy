package app

import com.devskiller.jfairy.Fairy
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.TemplateRenderer
import com.vaadin.flow.function.SerializableFunction
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

import java.time.LocalDate
import java.util.stream.Stream

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        // further config...
        app.run(args)
    }
}

@Route('')
@Slf4j
@Theme(Lumo)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends VerticalLayout {
    MainLayout() {
        def grid = new Grid<Item>()
        grid.setDataProvider(
                new AbstractBackEndDataProvider<Item, Object>() {
                    @Override
                    protected Stream<Item> fetchFromBackEnd(Query<Item, Object> query) {
                        return Item.TEST_DATA.stream().skip(query.offset).limit(query.limit)
                    }

                    @Override
                    protected int sizeInBackEnd(Query<Item, Object> query) {
                        return Item.TEST_DATA.size()
                    }

                    @Override
                    boolean isInMemory() {
                        false // BS
                    }
                }
        )
        grid.addColumn(new ComponentRenderer<Icon, Item>({
            it?.type?.buildComponent() ?: new Icon(VaadinIcon.COG)
        } as SerializableFunction<Item, Icon>)).setHeader("Type").setSortable(true).setFlexGrow(0)
        grid.addColumn(
                TemplateRenderer.<Item> of("""<strong>[[item.headline]]</strong><br /><small>[[item.preface]]</small>""")
                        .withProperty('headline', { it.headline })
                        .withProperty('preface', { it.preface })
        ).setHeader("Abstract").setSortable(true)
        grid.addColumn({ it.publication }).setHeader("Publication Date").setSortable(true).setFlexGrow(0)
        grid.setSizeFull()
        grid.element.themeList.addAll('wrap-cell-content', 'row-stripes')
        add(grid)
        setWidth('40rem')
        setHeight("100%")
    }
}

@Canonical
class Item {
    Type type
    String headline
    String preface
    LocalDate publication

    static enum Type {
        ARTICLE(VaadinIcon.PENCIL), BOOK(VaadinIcon.BOOK)
        final VaadinIcon icon

        Type(VaadinIcon icon) {
            this.icon = icon
        }

        Icon buildComponent() {
            new Icon(icon)
        }
    }

    private static final Fairy data = Fairy.create(Locale.ENGLISH)
    public static final List<Item> TEST_DATA = (0..100).collect {
        new Item(
                Type.ARTICLE,
                data.textProducer().latinSentence(),
                data.textProducer().paragraph(),
                data.dateProducer().randomDateInThePast(10).toLocalDate()
        )
    }
}
