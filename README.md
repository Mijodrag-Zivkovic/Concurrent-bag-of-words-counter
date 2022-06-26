# Concurrent-bag-of-words-counter
Concurrent solution to a problem of counting bag of words from text.
Bag of words is a certain combination of words (or just one word). Programm scans input file and counts occurences of all words/bag of words (of selected length). 
Gui with simple user interface included. </br>

This solution is divided into 3 components:

1. Input Component - input component runs first, and it allows user to select disk (imaginary disk, it's actually a folder named "disk") and 
   partitions of a disk (imaginary partitions, subfolders in this case). Component will then scan those partitions for any files and and pass file's 
   text to our next Component, Cruncher, for further operations. Input Component constantly scans for changes in selected directories, and if one happens, 
   will get it's text again and pass it to Cruncher. Number of input component threads is equal to number of (imaginary) disks.
   
2. Cruncher Component - this is where all the crunching happens, hence the name. Cruncher will receive text from input component and start counting words.
   Each cruncher has one attribute that separates it from other cruncher - arity. Arity is the size of bag of words. In other words (no pun intended), if arity is 1,
   then the cruncher will count occurences of every single word in a given text. If arity is 2, then cruncher calculates occurences of every pair of words in a given text,
   and so on. Arity is also unique cruncher identifier, which means there can be no more than one crunchers with certain arity. When calculation is done, cruncher will pass
   results to our nexr component, Output Component.
   
3. Output Component - this is the presentation layer of my solution. Whenever output recieves signal from cruncher that crunching has started, output displays name of the file,
   beggining with an asterisk symbol ( "&ast"). When cruncher is done, ti will send full results, and Output component will remove asterisk from file name, signaling users 
   that results are ready for displaying. User can then select file to see it's results, which will be shown on a graph in the middle of Gui.
   Output component also allows users to select a number of results, then sum them, which will display words with the most occurences from all selected files. 
   
When it comes to interconnectedness of components, Input Component can be connected to multiple crunchers from Cruncher Component, and vice versa. Only Output Component 
has a single thread, and is connected with all crunchers. </br>
Special request for this solution was to be able to handle 8 text files (sizes 70-100 mb) without using more than 4 gb of ram, which this solution successfully passes. </br>
In order to test this solution, find some text files and place them in any (imaginary) partition (In "disk1"/"disk2" folder, subfolders named using single capital letter).
