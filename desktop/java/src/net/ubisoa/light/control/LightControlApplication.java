package net.ubisoa.light.control;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
//WORK ON PROGRESS, DNS BROWSER
public class LightControlApplication implements BrowseListener {


	  public static void main(String[] args) throws DNSSDException, InterruptedException  {
	    System.out.println("TestBrowse Starting");
	    DNSSDService b = DNSSD.browse("_example._tcp", (LightControlApplication));
	    System.out.println("TestBrowse Running");
	    Thread.sleep(30000);
	    System.out.println("TestBrowse Stopping");
	    b.stop(  );
	    }
	
	// Display error message on failure
		  public void operationFailed(DNSSDService service, int errorCode)
		    {
		    System.out.println("Browse failed " + errorCode);
		    System.exit(-1);
		    }

		  // Display services we discover
		  public void serviceFound(DNSSDService browser, int flags, int ifIndex,
		            String name, String regType, String domain)
		    {
		    System.out.println("Add flags:" + flags + ", ifIndex:" + ifIndex +
		      ", Name:" + name + ", Type:" + regType + ", Domain:" + domain);
		    }

		  // Print a line when services go away
		  public void serviceLost(DNSSDService browser, int flags, int ifIndex,
		            String name, String regType, String domain)
		    {
		    System.out.println("Rmv flags:" + flags + ", ifIndex:" + ifIndex +
		      ", Name:" + name + ", Type:" + regType + ", Domain:" + domain);
		    }

}