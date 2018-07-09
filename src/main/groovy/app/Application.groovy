package app

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.Registration
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.time.LocalDate

@SpringBootApplication
class Application extends SpringBootServletInitializer {
    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        // further config...
        app.run(args)
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
        def field = new DateRangeField()
        def binder = new Binder<SomeModel>(SomeModel)
        binder.bind(field, 'dateRange')
        content.add(
                field,
                new HorizontalLayout(
                        new Button("New", {
                            binder.readBean(new SomeModel())
                        }),
                        new Button("Validate", {
                            notify(binder.valid.toString())
                        }),
                        new Button("Save", {
                            def b = new SomeModel()
                            binder.writeBean(b)
                            notify(b.toString())
                        }),
                ),
        )
    }

    void notify(String text) {
        Notification.show(text, 5000, Notification.Position.BOTTOM_STRETCH)
    }
}

@EqualsAndHashCode
@ToString
@TupleConstructor
class SomeModel {
    @Valid
    DateRange dateRange = new DateRange()
}

@EqualsAndHashCode
@ToString
@TupleConstructor
class DateRange {
    @NotNull
    LocalDate startDate = LocalDate.now()
    @NotNull
    LocalDate endDate = LocalDate.now()
}

class DateRangeComponent extends FormLayout {

    DatePicker start = new DatePicker(), end = new DatePicker()

    DateRangeComponent() {
        addFormItem(start, "Start")
        addFormItem(end, "End")
    }
}

class DateRangeField extends AbstractCompositeField<DateRangeComponent, DateRangeField, DateRange> {

    private Registration vclStart, vclEnd

    DateRangeField() {
        super(null)
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent)
        unregister()
        register()
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent)
        unregister()
    }

    private void register() {
        vclStart = content.start.addValueChangeListener {
            setModelValue(new DateRange(it.value, content.end.value), it.fromClient)
        }
        vclEnd = content.end.addValueChangeListener {
            setModelValue(new DateRange(content.start.value, it.value), it.fromClient)
        }
    }

    private void unregister() {
        vclEnd?.remove()
        vclStart?.remove()
        vclStart = vclEnd = null
    }

    @Override
    protected void setPresentationValue(DateRange newPresentationValue) {
        content.start.value = newPresentationValue.startDate
        content.end.value = newPresentationValue.endDate
    }

    @Override
    void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        // XXX does not work in Groovy: super.setRequiredIndicatorVisible(requiredIndicatorVisible)
        getElement().setProperty("required", requiredIndicatorVisible)
        [content.start, content.end]*.requiredIndicatorVisible = requiredIndicatorVisible
    }

    @Override
    void setReadOnly(boolean readOnly) {
        // XXX does not work in Groovy: super.setReadOnly(readOnly)
        getElement().setProperty("readonly", readOnly)
        [content.start, content.end]*.readOnly = readOnly
    }
}
