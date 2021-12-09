README
Final Project for CS501, Mobile App Development
Team member: Yash, Deyan, Harry, Aliya

Table of contents
1. Intro
2. Requirements/Getting Started
3. Testing
4. Using the app
5. Contact info
6. References
7. Extra Credit/Small Bonuses

1. Intro
Have you ever wanted to find peers taking the same classes as you to study with to tackle challenging assignments? Have you ever wanted to get rid of unnecessary textbooks? Have you ever wanted to buy your textbooks at a cheaper price? 
If so, BU Buddies is the perfect app for you!
Our app will let you easily connect and message with other students! Our algorithm allows you to view the profiles of students in the same classes as you. You can message with each other to discuss homework assignments and organize study sessions together. You can also visit our store to find used textbooks that other students are selling and put up your own items for sale. We have an easy checkout process that will allow you to contact product owners and purchase through google pay.

2. Requirements/Getting Started
Our app will require you to use a device/emulator with google play services for full functionality. There are no special instructions required when building. Our app uses firebase for authentication and data storage. If there is an issue with loading firebase data/services please contact us.

3. Testing
Feel free to create your own test accounts to use within our app. In case it helps here are the login info for some of the current users...
Usernames:
test@test.com
test2@test.com
test3@test.com
testing@test.com
vulcan@adc.com
jt@gmail.com
hyuan01@bu.edu
tgbubuddies@gmail.com
Aliya@bubuddies.com (This account was created in our live demo during the presentation)
deyan@bubuddies.com
pay@g2pay.com
cs411pr0jekt@gmail.com
Smith@gmail.com 

Password:
123123
(Should work for all of the test accounts)

4. Using the App:
After logging in to our app there are 4 main activities that can be accessed via the bottom menu:
Profile 
- View and edit your profile including your profile picture, name, year of graduation, classes, and bio
Pairing 
- View profiles of other users and choose to message them or find a new pairing
Messaging 
- View messages you have received from other users and message them back by clicking on the desired user's row to initiate messaging page. 
- Clicking on the profile pictures or the title of the messaging page will pop up the profile page for the specific user
- You can also choose to delete and block the user in the messaging page by clicking upper right button.
Store 
- View current items for sale and click on them to purchase*
- Use the floating + button to create your own listing
- Click on the opposing arrows to compare prices of textbooks on eBay

Contact us if you run into any issues or have any questions on use/functionality.

*to checkout (especially if you are using an emulator) you will need to log in to google play services

5. Contact Us
Emails: yshroff@bu.edu, deyanh@bu.edu, hyuan01@bu.edu, tzaliya@bu.edu
  
6. References
Picture Circle Transform: https://gist.github.com/julianshen/5829333
Recycler View Tutorial: https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui
Adding Google Pay to Android Apps: https://developers.google.com/pay/api/android/guides/tutorial 
Loading the Image to ImageView using URL: https://www.geeksforgeeks.org/how-to-load-any-image-from-url-without-using-any-dependency-in-android/
CardView using RecycleView: https://www.geeksforgeeks.org/cardview-using-recyclerview-in-android-with-example/

7. Extra Credit
- Reusing fragments with interface implementation (Bottom menu fragment and NewMsg Fragment)
- Used Recyclerview (Users.java, Chat.java, StoreActivity.java)
- Used Card View (StoreActivity.java)
- Good use of Menus (Chat.java, StoreActivity.java)
