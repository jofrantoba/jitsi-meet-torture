/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.meet.test;

import junit.framework.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.reflect.*;
import org.apache.tools.ant.taskdefs.optional.junit.*;
import org.openqa.selenium.*;

import java.io.*;

/**
 * Extends the xml formatter so we can detect failures and make screenshots
 * when this happen.
 * @author Damian Minkov
 */
public class FailureListener
    extends XMLJUnitResultFormatter
{
    private File outputParentFolder = null;

    /**
     * Creates a screenshot named by the class and name of the failure.
     * @param test the test
     * @param t the assertion error
     */
    @Override
    public void addFailure(Test test, AssertionFailedError t)
    {
        takeScreenshot(ConferenceFixture.focus,
            JUnitVersionHelper.getTestCaseClassName(test)
            + "." +JUnitVersionHelper.getTestCaseName(test)
            + "-focus.png");
        takeScreenshot(ConferenceFixture.secondParticipant,
            JUnitVersionHelper.getTestCaseClassName(test)
            + "." + JUnitVersionHelper.getTestCaseName(test)
            + "-participant.png");

        super.addFailure(test, t);
    }

    /**
     * Override to access parent output to get the destination folder.
     * @param out the xml formatter output
     */
    @Override
    public void setOutput(OutputStream out)
    {
        try
        {
            Object o = FieldUtils.readField(out, "file", true);
            if(o instanceof File)
                outputParentFolder
                    = new File(((File)o).getParent()
                        + File.separator + "screenshots");
        }
        catch(Exception e)
        {
            e.printStackTrace();

            outputParentFolder = new File("test-reports/screenshots");
        }

        super.setOutput(out);
    }

    /**
     * Takes screenshot for the supplied page.
     * @param driver the driver controlling the page.
     * @param fileName the destination screenshot file name.
     */
    private void takeScreenshot(WebDriver driver, String fileName)
    {
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;

        File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File destFile = new File(outputParentFolder, fileName);
        try
        {
            FileUtils.copyFile(scrFile, destFile);
        } catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }
}
