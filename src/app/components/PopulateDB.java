package app.components;

import java.util.Date;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.repositories.PartRepository;
import app.repositories.TypeRepository;
import app.repositories.UserRepository;
import app.entities.Part;
import app.entities.User;
import app.entities.Type;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

@Component
public class PopulateDB 
{
	@Autowired
	private PartRepository partRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private TypeRepository typeRepo;

	@Autowired
	private UserComponent userComp;

	@PostConstruct
	public void run()
	{
		System.out.println("Creating...");
		Part temp = new Part();
		String csvFile = "pcpartsdb.csv";
		String csvFile2 = "MOCK_DATA.csv";
		String csvFile3 = "types.csv";
        String line = "";
        String cvsSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile3))) {
        	line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] type = line.split(cvsSplitBy);

                try {
                    System.out.println("Type="+type[0]);
                    Type t = new Type();
                    t.setName(type[0]);
                    typeRepo.save(t);                    
                }
                catch(ArrayIndexOutOfBoundsException exception) {
                   break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile2))) {
        	line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] user = line.split(cvsSplitBy);

                try{
                System.out.println("User="+user[0]+" Password="+user[1]+" Email="+user[2] +" isAdmin=" + user[3]);
                    
                

        		List<String> errors = userComp.validate(user[0], user[2], user[1], user[1]);
        		if (errors.size() > 0) {
        			System.out.println(errors.toString());
        		}
        		userComp.create(user[0], user[2], user[1], "", true, user[3].equals("TRUE")? true : false);
        		
                }
                catch(ArrayIndexOutOfBoundsException exception) {
                   break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
               
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] part = line.split(cvsSplitBy);

                try{
                
                System.out.println("Type="+part[0]+" Name="+part[1]+" Price="+part[2]+" Description="+part[3] );
                    
                Part p = new Part();
                p.setType(typeRepo.findByName(part[0]));
                p.setName(part[1]);
                p.setPrice(Double.parseDouble(part[2]));
                p.setDescription(part[3]);
                p.setSeller(userRepo.findOne(new Double(Math.random() * ((50) + 1)).longValue()));
                partRepo.save(p);
                }
                catch(ArrayIndexOutOfBoundsException exception) {
                   break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }                
	}				
}