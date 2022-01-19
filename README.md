# stranded
Portfolio project

Stranded is a showcase project intended to show off my android development chops. Ostensibly it is a game about dialogue where the player inhabits an AI and "talks" with a character. Think of it like a dialogue tree from an RPG except here it's expanded to the entire game. And I'm not gonna spoil any more than that! You'll just have to play it and see for yourself! ;)

DESIGN PHILOSOPHY BELOW:

The "homepage" if you will is the ChatPageFragment which as you might have guessed is where the player actually plays the game. The only other pages (Fragments) are SettingsFragment, AboutFragment and endingFragment (just a credits screen for when you beat the game). If your wondering the AboutFragment just describes the app, gives my email address and a github link. Anyway The ChatPageFragment is where the magic happens on the ui front. The main logic hub is the ChatPageViewModel, this ViewModel contains like 90% of the actual logic needed to run the app. With all the other components acting in support. When your examining the code I recommend you start with the ChatPageFragment and ViewModel then branch out from there.

DESIGN OVERVIEW:

The goal of the app is to run the user through the scripted dialogue tree, presenting them with dialogue choices and running scripted sound effects/animations along the way. On a macro scale this is done by having a preloaded Room database that contains all the data for the entire narrative. Every line, user prompt (dialogue choice), and trigger (for sounds and animations). Every line and prompt line also knows what comes next. That is to say Every line in the app is a single database entry that includes data determining which line should be shown next. (For a more detailed explanation look at the top comment in the scriptDatabaseEntities file app/src/main/java/com/example/stranded/database/scriptDatabaseEntities).

This design allows the app itself to essentially act as a machine that executes on the narrative it is given. If I wanted to I could go in and change the narrative in the database and the app would seamlessly run any new additions or other changes I made (assuming it's uninstalled and reinstalled to reload the db). I can also change or add Triggers that play new animations or sound effects at different times, and since all the app does is programatically execute the narrative Sequence it was given, it can handle that! To be fair adding or removing things from the database is a nightmare. The goal from the beginning was to make a prepopulated database with the entire story, then never change it. But after that grueling task is done the app can be as dynamic as I need it to be!

DESIGN DETAILS:

While I did my best to go through the entire codebase and comment everything with as much detail as I could. There are certainly things I missed or just didn't explain enough, perhaps I missed a detail that is leaving you confused as to what a given piece of code is for. If you have any questions don't hesitate to start a discussion on this github or email me (austinoguy51@gmail.com). I'm still learning and appreciate any advice or observations you may have!
