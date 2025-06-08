import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;



public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;

    public WeatherAppGui(){
        //setup gui and create title
        super("Weather App");

        //make it so gui ends programs process when it is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //gui size
        setSize(450, 650);

        // load gui center screen
        setLocationRelativeTo(null);
        
        // make it so we can manually position elements in the gui
        setLayout(null);

        // make it so gui can't be resized
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // add search box
        JTextField searchTextField = new JTextField();

        // set location and size of search box
        searchTextField.setBounds(15, 15, 351, 45);

        // change font style and font size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/weatherapp_images/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text 
        JLabel temperatureText = new JLabel("100 F");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/weatherapp_images/windspeed.png"));
        windspeedImage.setBounds(15, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 10mph</html>");
        windspeedText.setBounds(90, 500, 90, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // add search button
        JButton searchButton = new JButton(loadImage("/Users/jordanreitz/Library/Mobile Documents/com~apple~CloudDocs/School/cse 310/Assignments/Module 3/Weather/WeatherApp/src/assets/weatherapp_images/search.png"));

        // Change cursor when hovering over button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get input location
                String userInput = searchTextField.getText();

                System.out.println("📥 User input: " + userInput);

                // remove whitespace
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // get weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    System.out.println("Could not retrieve weather data.");
                    return;
                }

                //update image
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){
                    case "Clear":
                    weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/clear.png"));
                    break;
                    case "Cloudy":
                    weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/cloudy.png"));
                    break;
                    case "Rain":
                    weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/rain.png"));
                    break;
                    case "Snow":
                    weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/snow.png"));
                    break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " F");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "mph</html>");
            }


        });
        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));

            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
