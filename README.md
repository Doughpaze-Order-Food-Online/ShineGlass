# ShineGlass
Glass Sotre App to order glass furniture items online

## Status
- Developing

## Main Componets of the Project
- Android app
- Admin Panel
- Server

## Main Features used while Developing
- Splash screen
- Recycler Views with GridLayoutManager
- Fragments
- Bottom Navigation Menu
- Side Navigation Menu
- ViewPager with Image Slider
- Alert Dialogue, NumberPicker, Popup activity

## Development Approach
### Programming Languages Used
- xml
- Java
- JavaScript
- CSS
### Team project
  #### Members
  - Siddhant Khobragade - Working majorly over Front-end.
  - Harish Jartarghar - Lead Backend Developer
### Procedure
- The User Interface is designed with reference to the psd files provided from the client side's designer.
- Problems and bugs faced while developing are resolved by searching the issues on websites like Stackoverflow, Youtube and Official Android developer site.
- themes, colors and icons used are taken from psd designs provided by the designer. 
- Main(starting) xml designs with respective to front-end java code and activities were created first and then connected with back-end respectively with complete cordination and frequent testing. 

## Main Technologies used while Developing
- Material Design Widgets
- API calls
- Networking and Jason Parsing
- User Login/Signup Authentication
- Otp verification Via Mobile Number.

## Server Side 
- The server is developed using nodejs backend tool
- Express Framework is used to implement rest apis.
- MongoDb is used as the data storage that is database.
- Mongoose package is used as agent to the mongodb database.
- Other packages are used based on the requirements in the applcation.
- Apis are implemented based on the requirements of the app.

## Implementations and Libraries used
### API request libraries
- implementation 'com.squareup.retrofit2:retrofit:2.4.0'
- implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
- implementation 'com.squareup.retrofit2:adapter-rxjava:2.1.0'
- implementation 'io.reactivex:rxjava:1.2.0'
- implementation 'io.reactivex:rxandroid:1.2.1'
- testImplementation 'junit:junit:4.13'

### glide
- implementation 'com.github.bumptech.glide:glide:4.11.0'
- annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
- implementation 'org.apache.commons:commons-io:1.3.2'

### google signin
- implementation 'com.google.android.gms:play-services-auth:18.1.0'

### facbook login
- implementation 'com.facebook.android:facebook-login:5.15.3'

### circular ImageView
- implementation 'de.hdodenhof:circleimageview:3.1.0'

### User Instructions
- Do not use Login with OTP although its authentication is implemented completely due to limited paid resources.
- Till now Please Login with Google or login manually As the facebook authentication is not yet fully implemented.







