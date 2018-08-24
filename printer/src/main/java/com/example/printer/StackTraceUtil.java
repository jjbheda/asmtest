

package com.example.printer;


import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Utility related with stack trace.
 */
public class StackTraceUtil {

  private static final String XLOG_STACK_TRACE_ORIGIN;

  static {
    // Let's start from xlog library.
    String xlogClassName = StackTraceUtil.class.getName();
    XLOG_STACK_TRACE_ORIGIN = xlogClassName.substring(0, xlogClassName.lastIndexOf('.') + 1);
  }

  /**
   * Get a loggable stack trace from a Throwable
   *
   * @param tr An exception to log
   */
  public static String getStackTraceString(Throwable tr) {
    if (tr == null) {
      return "";
    }

    // This is to reduce the amount of log spew that apps do in the non-error
    // condition of the network being unavailable.
    Throwable t = tr;
    while (t != null) {
      if (t instanceof UnknownHostException) {
        return "";
      }
      t = t.getCause();
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    tr.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }

  /**
   * Get the real stack trace and then crop it with a max depth.
   *
   * @param stackTrace the full stack trace
   * @param maxDepth   the max depth of real stack trace that will be cropped, 0 means no limitation
   * @return the cropped real stack trace
   */
  public static StackTraceElement[] getCroppedRealStackTrack(StackTraceElement[] stackTrace,
                                                             String stackTraceOrigin,
                                                             int maxDepth) {
    return cropStackTrace(getRealStackTrack(stackTrace, stackTraceOrigin), maxDepth);
  }

  /**
   * Get the real stack trace, all elements that come from LogStorer library would be dropped.
   *
   * @param stackTrace the full stack trace
   * @return the real stack trace, all elements come from system and library user
   */
  private static StackTraceElement[] getRealStackTrack(StackTraceElement[] stackTrace,
                                                       String stackTraceOrigin) {
    int ignoreDepth = 0;
    int allDepth = stackTrace.length;
    String className;
    for (int i = allDepth - 1; i >= 0; i--) {
      className = stackTrace[i].getClassName();
      if (className.startsWith(XLOG_STACK_TRACE_ORIGIN)
          || (stackTraceOrigin != null && className.startsWith(stackTraceOrigin))) {
        ignoreDepth = i + 1;
        break;
      }
    }
    int realDepth = allDepth - ignoreDepth;
    StackTraceElement[] realStack = new StackTraceElement[realDepth];
    System.arraycopy(stackTrace, ignoreDepth, realStack, 0, realDepth);
    return realStack;
  }

  /**
   * Crop the stack trace with a max depth.
   *
   * @param callStack the original stack trace
   * @param maxDepth  the max depth of real stack trace that will be cropped,
   *                  0 means no limitation
   * @return the cropped stack trace
   */
  private static StackTraceElement[] cropStackTrace(StackTraceElement[] callStack,
                                                    int maxDepth) {
    int realDepth = callStack.length;
    if (maxDepth > 0) {
      realDepth = Math.min(maxDepth, realDepth);
    }
    StackTraceElement[] realStack = new StackTraceElement[realDepth];
    System.arraycopy(callStack, 0, realStack, 0, realDepth);
    return realStack;
  }

  public static String format(StackTraceElement[] stackTrace) {
    StringBuilder sb = new StringBuilder(256);
    if (stackTrace == null || stackTrace.length == 0) {
      return null;
    } else if (stackTrace.length == 1) {
      return "\t─ " + stackTrace[0].toString();
    } else {
      for (int i = 0, N = stackTrace.length; i < N; i++) {
        if (i != N - 1) {
          sb.append(stackTrace[i].toString());
          sb.append(getLineSeparator());
        } else {
          sb.append(stackTrace[i].toString());
        }
      }
      return sb.toString();
    }
  }


  public static String getLineSeparator(){
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return "\n";
    }
    return System.lineSeparator();
  }
}
