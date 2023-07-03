package com.group31.editor.util;

import io.sentry.Breadcrumb;
import io.sentry.Sentry;

public class SentryReporting {

  public enum BREADCRUMB_TYPE {
    NAVIGATION,
    DEBUG,
    ERROR,
    INFO,
    QUERY,
    UI,
    USER,
    USERINTERACTION,
    DEFAULT,
  }

  public SentryReporting() {
    init();
  }

  public SentryReporting(Boolean optIn) {
    if (optIn) init(); else Logger.log(
      "{sentry.io} Reporting is disabled ",
      Logger.LOG_TYPE.WARN,
      Logger.COLOUR_SET.ERROR
    );
  }

  private void init() {
    Logger.log(
      "{sentry.io} Contacting the mothership 👽🛸",
      Logger.LOG_TYPE.WARN,
      Logger.COLOUR_SET.INFO
    );
    Sentry.init(options -> {
      options.setDsn(
        "https://7bc446ba05a24ba3ae9ef7f93fce270e@o269605.ingest.sentry.io/4504296483913728"
      );
      options.setTracesSampleRate(1.0);
      options.setDebug(false);
      options.setEnvironment("production");
    });
  }

  public static void leaveABreadcrumb(
    BREADCRUMB_TYPE type,
    String messageOrNavTo,
    String categoryOrNavFrom
  ) {
    Breadcrumb breadcrumb;
    switch (type) {
      case DEBUG:
        breadcrumb = Breadcrumb.debug(messageOrNavTo);
        break;
      case ERROR:
        breadcrumb = Breadcrumb.error(messageOrNavTo);
        break;
      case INFO:
        breadcrumb = Breadcrumb.info(messageOrNavTo);
        break;
      case QUERY:
        breadcrumb = Breadcrumb.query(messageOrNavTo);
        break;
      case NAVIGATION:
        breadcrumb = Breadcrumb.navigation(messageOrNavTo, categoryOrNavFrom);
        break;
      case USER:
      case UI:
        breadcrumb = Breadcrumb.ui(categoryOrNavFrom, messageOrNavTo);
        break;
      default:
        breadcrumb = new Breadcrumb(messageOrNavTo);
        break;
    }
    switch (type) {
      case NAVIGATION:
      case USER:
      case UI:
        break;
      case DEBUG:
      case ERROR:
      case INFO:
      case QUERY:
      default:
        breadcrumb.setCategory(categoryOrNavFrom);
        break;
    }
    Sentry.addBreadcrumb(breadcrumb);
  }

  public static void leaveABreadcrumb(
    BREADCRUMB_TYPE type,
    String subCategory,
    String viewId,
    String viewClass
  ) {
    Sentry.addBreadcrumb(Breadcrumb.userInteraction(subCategory, viewId, viewClass));
  }

  public static void toolBreadcrumb(String oldTool, String newTool) {
    Breadcrumb breadcrumb = Breadcrumb.navigation(oldTool, newTool);
    breadcrumb.setMessage("Tool set to " + newTool);
    Sentry.addBreadcrumb(breadcrumb);
  }

  public static void layerBreadcrumb(Integer oldLayer, Integer currentLayer) {
    Breadcrumb breadcrumb = Breadcrumb.navigation(oldLayer.toString(), currentLayer.toString());
    breadcrumb.setMessage("Layer set to " + currentLayer);
    Sentry.addBreadcrumb(breadcrumb);
  }
}
