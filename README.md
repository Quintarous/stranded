# stranded
Portfolio project

Stranded is a showcase project intended to show off my android development chops. Ostensibly it is a game about dialogue where the player inhabits an AI and "talks" with a character. Think of it like a dialogue tree from an RPG except here it's expanded to the entire game. And I'm not gonna spoil any more than that! You'll just have to play it and see for yourself! ;)

DESIGN PHILOSOPHY BELOW:

The "homepage" if you will is the ChatPageFragment which as you might have guessed is where the player actually plays the game. The only other pages (Fragments) are SettingsFragment and AboutFragment. If your wondering the AboutFragment just describes the app gives my email address and a github link. Anyway The ChatPageFragment is where the magic happens on the ui front. The main logic hub is the ChatPageViewModel, this ViewModel contains like 90% of the actual logic needed to run the app. With all the other components acting in support. When your examining the code I recommend you start with the ChatPageFragment and ViewModel then branch out from there.

Design overview:

The goal of the app is to run the user through the scripted dialogue tree, presenting them with dialogue choices along the way. On a macro scale this is done by having a preloaded Room database that contains all the data for the entire narrative. Every line, user prompt (dialogue choice), and trigger (for sounds and animations). Every line and prompt line also knows what comes next.