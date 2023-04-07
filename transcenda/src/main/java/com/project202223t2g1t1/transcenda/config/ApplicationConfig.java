package com.project202223t2g1t1.transcenda.config;


import com.project202223t2g1t1.transcenda.Campaign.Campaign;
import com.project202223t2g1t1.transcenda.Campaign.CampaignRepository;
import com.project202223t2g1t1.transcenda.Card.*;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusion;
import com.project202223t2g1t1.transcenda.MerchantExclusion.MerchantCategoryCodeExclusionRepository;
import com.project202223t2g1t1.transcenda.Security.AESEncryptionUtil;
import com.project202223t2g1t1.transcenda.User.User;
import com.project202223t2g1t1.transcenda.User.UserRepository;
import com.project202223t2g1t1.transcenda.User.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@AllArgsConstructor
public class ApplicationConfig {
    //temp encoder to create an encrypted password
    private final UserRepository userRepository;

    @Autowired
    private AESEncryptionUtil aesEncryptionUtil;


    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username) //fetch user from database
                .orElseThrow(()-> new UsernameNotFoundException("User with email " + username + " not found"));
    }



    @Bean
    public AuthenticationProvider authenticationProvider(){
        //data access object that helps to access user details
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }



    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, CardProgramRepository cardProgramRepository, CardProgramRewardRateRepository cardProgramRewardRateRepository,
                                        CardRepository cardRepository, CampaignRepository campaignRepository, MerchantCategoryCodeExclusionRepository merchantCategoryCodeExclusionRepository) throws IOException {
        //create dummy users
        String encodedPassword = bCryptPasswordEncoder().encode("123");
        return args -> {
            User xz = new User(
                    "Zhao Xing",
                    "Chen",
                    "chenzhaoxing.98@gmail.com",
                    encodedPassword,
                    UserRole.ADMIN
            );
//            User tester = new User(
//                    "CarbonO",
//                    "Testing",
//                    "carbonohelp@gmail.com",
//                    encodedPassword,
//                    UserRole.ADMIN,
//                    true
//            );
            userRepository.saveAll(
                    List.of(xz)
            );

            // create existing card programs
            List<CardProgram> cardPrograms = new ArrayList<>();
            CardProgram scis_shopping = new CardProgram("scis_shopping","Standalone rewards program", RewardType.POINTS);
            CardProgram scis_premiummiles = new CardProgram("scis_premiummiles","SCISMiles program", RewardType.MILES);
            CardProgram scis_platinummiles = new CardProgram("scis_platinummiles","SCISMiles program", RewardType.MILES);
            CardProgram scis_freedom = new CardProgram("scis_freedom","SCIS Cashback program", RewardType.CASHBACK);

            cardPrograms.add(scis_shopping);
            cardPrograms.add(scis_premiummiles);
            cardPrograms.add(scis_platinummiles);
            cardPrograms.add(scis_freedom);

            cardProgramRepository.saveAll(cardPrograms);

            // create dummy card program reward rates
            List<CardProgramRewardRate> cardProgramRewardRates = new ArrayList<>();
            // SCIS_Shopping
            cardProgramRewardRates.add(new CardProgramRewardRate(1.0,MerchantCategory.OTHER,scis_shopping));
            cardProgramRewardRates.add(new CardProgramRewardRate(4.0, MerchantCategory.SHOPPING, scis_shopping));
            // SCIS_Shopping bonus
            cardProgramRewardRates.add(new CardProgramRewardRate(10.0, MerchantCategory.ONLINE_SHOPPING, scis_shopping));

            //SCIS_premiummiles
            cardProgramRewardRates.add(new CardProgramRewardRate(1.1, MerchantCategory.OTHER, scis_premiummiles));
            cardProgramRewardRates.add(new CardProgramRewardRate(2.2, MerchantCategory.FOREIGN_OTHER, scis_premiummiles));
            //SCIS_premiummiles bonus
            cardProgramRewardRates.add(new CardProgramRewardRate(3.0, MerchantCategory.HOTEL, scis_premiummiles));

            // SCIS_platinummiles
            cardProgramRewardRates.add(new CardProgramRewardRate(1.4, MerchantCategory.OTHER, scis_platinummiles));
            cardProgramRewardRates.add(new CardProgramRewardRate(3.0, MerchantCategory.FOREIGN_OTHER, scis_platinummiles));
            //SCIS_platinummiles bonus
            cardProgramRewardRates.add(new CardProgramRewardRate(3.0, MerchantCategory.HOTEL, scis_platinummiles));
            cardProgramRewardRates.add(new CardProgramRewardRate(6.0, MerchantCategory.FOREIGN_HOTEL, scis_platinummiles));

            // SCIS_freedom
            cardProgramRewardRates.add(new CardProgramRewardRate(0.5, MerchantCategory.CASHBACK, scis_freedom));
            cardProgramRewardRates.add(new CardProgramRewardRate(1.0, MerchantCategory.CASHBACK_500, scis_freedom));
            cardProgramRewardRates.add(new CardProgramRewardRate(3.0, MerchantCategory.CASHBACK_2000, scis_freedom));


            cardProgramRewardRateRepository.saveAll(cardProgramRewardRates);

            // create dummy cards NOTE: card number will be encrypted in the future
//            List<Card> cards = new ArrayList<>();
//            Card card1 = new Card("23432c85-bcca-486e-89d5-86371397d83a", scis_freedom, "Lydia@transcenda.com"); // user id taken from cognito
//            Card card2 = new Card("c2ef544b-6155-4069-9cd1-04522f3390d6", scis_platinummiles, "Lydia@transcenda.com");
//            Card card3 = new Card("17cc3eca-c920-4e83-83fd-e1bb9f89ca75", scis_shopping, "Lydia@transcenda.com");
//            Card card4 = new Card("57fc1e82-ad46-4d1e-8dea-46945f8835bf", scis_premiummiles, "Lydia@transcenda.com");

//            cards.add(card1);
//            cards.add(card2);
//            cards.add(card3);
//            cards.add(card4);
//
//            cardRepository.saveAll(cards);

            // import dummy cards from csv file
//            String csvFile = "C:/Users/Newbieshine/Desktop/Cs301 files/User-Card-Number.csv";

            InputStream input = ApplicationConfig.class.getResourceAsStream("/User-Card-Number.csv");
            String line;
            String csvDelimiter = ",";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
                // clear headers
                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(csvDelimiter);
                    // Process each line of the CSV file here
                    if (values[1].equals("scis_freedom")){
                        Card card = new Card(values[0], scis_freedom, values[2],values[3],aesEncryptionUtil );
                        cardRepository.save(card);
                    } else if (values[1].equals("scis_platinummiles")){
                        Card card = new Card(values[0], scis_platinummiles, values[2],values[3],aesEncryptionUtil);
                        cardRepository.save(card);
                    } else if (values[1].equals("scis_shopping")){
                        Card card = new Card(values[0], scis_shopping, values[2],values[3], aesEncryptionUtil);
                        cardRepository.save(card);
                    } else if (values[1].equals("scis_premiummiles")){
                        Card card = new Card(values[0], scis_premiummiles, values[2],values[3], aesEncryptionUtil);
                        cardRepository.save(card);
                    }

                    System.out.println(values[0] + "," + values[1] + "," + values[2] + "," + values[3]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create dummy campaigns
            List<Campaign> campaigns = new ArrayList<>(); //date put as 2020 for testing purposes
            Campaign blanda_campaign = new Campaign("Blanda Campaign", "5% cashback on all purchases with Blanda, minimum $50",
                    LocalDate.of(2020,3,15), LocalDate.of(2023,6,30),
                    "Blanda", 0.05, RewardType.CASHBACK, 50,"scis_freedom",scis_freedom );

            campaigns.add(blanda_campaign);

            campaignRepository.saveAll(campaigns);

            //Create default exclusions
            List<MerchantCategoryCodeExclusion> mccExclusions = new ArrayList<>();
            mccExclusions.add(new MerchantCategoryCodeExclusion(6051,
                    "Quasi Cash Merchants: Prepaid top-ups", "GrabPay Top-ups, FEVO Mastercards"));
            mccExclusions.add(new MerchantCategoryCodeExclusion(9399,
                    "Government Services–Not Elsewhere Classified | Excluded", "AXS Payments"));
            mccExclusions.add(new MerchantCategoryCodeExclusion(6540,
                    "POI (Point of Interaction) Funding Transactions (Excluding MoneySend) | Taxis & public transport", "Ez-Link Top-ups"));

//             for presentation introduction
            mccExclusions.add(new MerchantCategoryCodeExclusion(9000,
                    "Example Test", "MerchantA"));


            merchantCategoryCodeExclusionRepository.saveAll(mccExclusions);

        };
    }
}
