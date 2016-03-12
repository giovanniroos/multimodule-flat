package za.dsc.grp.lib.common.util.conversion;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;

public class TextHtmlConverterTestCase {

  @Test
  public void testToHTML() {
    TextHtmlConverter converter = new TextHtmlConverter();
    String input = "Dear Ms Afonso\n\n" +
        "Group scheme name: Nosihle Telecommunications\n" +
        "Group scheme number: 6600005695\n" +
        "\n" +
        "We have completed the installation for this scheme \n\n" +
        "The installation for the above-mentioned scheme has been completed. We have attached the following " +
        "documents: \n\n" +
        "•\tInstallation letter\n" +
        "•\tClient benefit schedule\n" +
        "•\tPremium statement\n" +
        "•\tMember benefit schedules\n" +
        "•\tUnderwriting requirement letters\n\n" +
        "We will process the commission and pay it monthly once we have collected a successful debit order for this scheme.\n" +
        "If you have any queries please contact me or our call centre. Please use the group scheme name and number as" +
        " the reference.\n\n" +
        "Kind regards\n\n" +
        "Jannie & team \n" +
        "Servicing Administrator\n" +
        "Group Risk Administration \n\n" +
        "Tel: 0115295722\n" +
        "Fax: 0115397822\n" +
        "Email: jannie@discovery.co.za \n\n" +
        "Call centre: 0860 0 GROUP (0860 0 47687)\n" +
        "Email: groupinfo@discovery.co.za";

    String html = converter.toHTML(input);
    Assert.assertEquals("<p>Dear Ms Afonso</p><p>Group scheme name: Nosihle Telecommunications</br>Group scheme " +
        "number: 6600005695</p><p>We have completed the installation for this scheme </p><p>The installation for the " +
        "above-mentioned scheme has been completed. We have attached the following documents: </p><p>&#8226;\tInstallation letter</br>&#8226;\tClient benefit schedule</br>&#8226;\tPremium statement</br>&#8226;\tMember benefit schedules</br>&#8226;\tUnderwriting requirement letters</p><p>We will process the commission and pay it monthly once we have collected a successful debit order for this scheme.</br>If you have any queries please contact me or our call centre. Please use the group scheme name and number as the reference.</p><p>Kind regards</p><p>Jannie & team </br>Servicing Administrator</br>Group Risk Administration </p><p>Tel: 0115295722</br>Fax: 0115397822</br>Email: jannie@discovery.co.za </p><p>Call centre: 0860 0 GROUP (0860 0 47687)</br>Email: groupinfo@discovery.co.za</p></br>", html);
  }
}
