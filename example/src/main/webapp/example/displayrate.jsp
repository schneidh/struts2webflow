<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Price Quote</title>
</head>

<body>
<h3>Acme Price Quote</h3>
<p>
  <s:form action="FlowAction" method="get">
  	<s:hidden name="eventId" value="finish"/>
    Your monthly premium would be $<s:property value="rate"/>
    <s:submit value="Next"/>
  </s:form>
</p>
</body>
</html>
