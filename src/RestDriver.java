
public class RestDriver 
{
	public static void main(String[] args) throws Exception
	{
		int portNumber = 9999;
		String restClassPackage = "app.rest";
		String applicationContext = "applicationContext_jpa.xml";

		new JerseyStarter().start(portNumber, restClassPackage, applicationContext);
	}
}
