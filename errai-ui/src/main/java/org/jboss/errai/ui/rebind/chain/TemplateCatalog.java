package org.jboss.errai.ui.rebind.chain;

import org.apache.commons.io.IOUtils;
import org.apache.stanbol.enhancer.engines.htmlextractor.impl.DOMBuilder;
import org.jboss.errai.ui.shared.DomVisit;
import org.jboss.errai.ui.shared.DomVisitor;
import org.jboss.errai.ui.shared.chain.Chain;
import org.jboss.errai.ui.shared.chain.Command;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.net.URL;

/**
 * @author edewit@redhat.com
 */
public class TemplateCatalog {
  private Chain chain = new Chain();

  public static TemplateCatalog createTemplateCatalog(Command... commands) {
    TemplateCatalog catalog = new TemplateCatalog();
    for (Command command : commands) {
      catalog.chain.addCommand(command);
    }
    return catalog;
  }

  public Document visitTemplate(URL template) {
    final Document document = parseTemplate(template);
    for (int i = 0; i < document.getChildNodes().getLength(); i++) {
      final Node node = document.getChildNodes().item(i);
      if (node instanceof Element) {
        DomVisit.visit((Element) node, new DomVisitor() {
          @Override
          public boolean visit(Element element) {
            chain.execute(element);
            return true;
          }
        });
      }
    }
    return document;
  }

  /**
   * Parses the template into a document.
   *
   * @param template the location of the template to parse
   */
  public Document parseTemplate(URL template) {
    InputStream inputStream = null;
    try {
      inputStream = template.openStream();
      return DOMBuilder.jsoup2DOM(Jsoup.parse(inputStream, "UTF-8", ""));
    } catch (Exception e) {
      throw new IllegalArgumentException("could not read template " + template, e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  /**
   * for testing purposes.
   *
   * @return the initialized chain
   */
  Chain getChain() {
    return chain;
  }
}
