package app

import com.sun.xml.internal.bind.v2.TODO
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.polymertemplate.EventHandler
import com.vaadin.flow.component.polymertemplate.Id
import com.vaadin.flow.component.polymertemplate.ModelItem
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.templatemodel.Include
import com.vaadin.flow.templatemodel.TemplateModel
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
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
        new SpringApplication(Application).tap{
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
@Theme(Lumo)
@HtmlImport('frontend:///styles.html')
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class MainLayout extends Composite<Div> {
    MainLayout() {
        def tagSelect = new TagSelectComponent().tap{
            selected = [
                    new TagSelectComponent.Tag("x", "X"),
                    new TagSelectComponent.Tag("y", "Y"),
            ].toSet()
            dataProvider = ListDataProvider.ofCollection([
                    new TagSelectComponent.Tag("x", "X"),
                    new TagSelectComponent.Tag("y", "Y"),
                    new TagSelectComponent.Tag("z", "Z"),
            ])
        }
        content.add(
                new H1("Tag Select"),
                new Div(
                        new Checkbox("Horizontal?").tap {
                            addValueChangeListener{
                                tagSelect.element.themeList.set('horizontal', it.value)
                            }
                        },
                ),
                tagSelect
        )
    }
}


@com.vaadin.flow.component.Tag('tag-select')
@HtmlImport("tag-select.html")
class TagSelectComponent extends PolymerTemplate<TagSelectModel> {

    @Id('select')
    ComboBox<Tag> select

    private Registration registration

    @TupleConstructor
    @ToString
    @EqualsAndHashCode(includes = ['id'])
    static class Tag {
        String id
        String label
    }

    static interface TagSelectModel extends TemplateModel {
        @Include(["id","label"])
        void setSelected(List<Tag> tags)
        List<Tag> getSelected()
    }

    @EventHandler
    void deselect(@ModelItem Tag tag) {
        selected = selected.findAll{ it.id!=tag.id }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent)
        select.tap {
            itemLabelGenerator = { it.label }
            registration = addValueChangeListener {
                if (it.value) {
                    selected = selected + [it.value]
                    value = null // FIXME: triggers value change again and seems to roundtrip
                }
            }
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent)
        if (registration) {
            registration.remove()
            registration = null
        }
    }

    // TODO: can we delegate to a super class method?

    void setSelected(Set<Tag> tags) {
        model.setSelected(tags.toList())
    }

    Set<Tag> getSelected() {
        model.selected.toSet()
    }

    void setDataProvider(DataProvider<Tag, String> dataProvider) {
        select.dataProvider = dataProvider
    }

}
