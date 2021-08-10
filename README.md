# Discord Chat Bot For Minecraft 1.17.1
This chatbot was written for my friends server, so we can chat even when one of us can't be ingame.
Built on PaperMC

### Prerequisites
1. Discord server; you should have admin rights there
2. Discord bot; there is plenty of tutorials out there, so if you need one, look it up

### Installation
1. Download the GreyChat.jar from releases
2. Copy this jar into the plugins folder of your Paper server
3. Start the server
4. Once the server loads up, shut it down again
5. In your server folder, new folder is present "Greychat"; go inside and open config.json
6. Replace the TOKEN and CHANNEL_ID with the token of your bot and id of the channel you want to link the  bot to
7. Start the server
Optional: 8. if you want the `| deaths` command to function, you need to create a scoreboard `/scoreboard objectives add Deaths deathCount`, name has to be "Deaths", otherwise the bot won't pick it up.

Enjoy!

### Commands
Run `| help`
To get all available commands to you
