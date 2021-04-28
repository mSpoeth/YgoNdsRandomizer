## Randomizer for the Yu-Gi-Oh games on the Nintendo DS.

A tool to load up an .nds rom file and randomise the content each card pack and structure deck holds.
The seed, along with the used settings, can be saved and later imported.

It was created for WC2011 specifically, However, all games 2008 and up should work, due to their similar structure.

The current options of randomization allow to:

### **Packs:**
  - Each card may appear in any pack with original rarities.
  - Each card may appear in any pack with new rarities. (Commons stay commons)
  - No changes.
  
### **Structure Decks:**
  - All cards contained in structure decks will be redistributed randomly among all decks.
  - No changes.

### **Technical limitations:**
  Packs:
  Every card has a primary and a secondary pack it appears in. For example, Cyber Dragon is a Super Rare in
  Cybernetic Revolution and a Rare in Synchro Awaken. Every pack is one of three possible types: It's either a
  Main Set (Dark Beginning 1 to Storm of Ragnarok), a Side Set (Synchro Awaken to World Championship Edition 11)
  or a Turbo Duel Set. A card's primary pack may be of any set, its secondary, however, can only be a Side Set.
  As such, every card's secondary appearance, if any, is limited to those Side Sets.
  
  Structure Decks:
  The games don't have any files that dictate the contents of what's inside the Structure Deck when you buy one.
  It simply gives the player a copy of every card of the corresponding Deck Recipe. Because of this, you can always
  see what the randomized deck contains, just by looking up its Sample Recipe.

### **How to use:**
  To start the app with the User Interface, simply execute the .jar file. If the app is ran with any parameters,
  no UI will open and the entire process happens on command line only.
  To see all available parameters, run with -h.

### **Licensing:**
This project is open-source and most classes can be used as tools and adapted. For example, you can use it to
modify other aspects of the game or to randomize the other Yugioh games featured on the Nintendo DS, by repurposing
the unwrappers or adding additional randomizers.

Feel free to use the code however you want!


#### This project is licensed under the terms of the MIT license.
