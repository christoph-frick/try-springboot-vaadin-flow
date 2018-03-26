package app;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.theme.AbstractTheme;

import java.util.Collections;
import java.util.List;

@HtmlImport("frontend://bower_components/vaadin-material-theme/color.html")
@HtmlImport("frontend://bower_components/vaadin-material-theme/typography.html")
@HtmlImport("frontend://bower_components/vaadin-material-theme/font-icons.html")
public class Material implements AbstractTheme {

    @Override
    public String getBaseUrl() {
        return "src/";
    }

    @Override
    public String getThemeUrl() {
        return "theme/material/";
    }

    @Override
    public List<String> getBodyInlineContents() {
        return Collections.singletonList("<custom-style>\n"
                + "    <style include=\"material-color material-typography\"></style>\n"
                + "</custom-style>");
    }
}
