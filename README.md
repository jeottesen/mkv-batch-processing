# mkv-batch-processing
I made this in 2013 to process large amounts of mkv files easier.
I wanted to remove extra audio tracks and add external subs for files. It is also good for setting default tracks. 

## File Paths
To use it you need to modify the a few lines in the `AudioRemover.java` file then recompile it.

```
MKVMergePaths Paths = new MKVMergePaths("C:\\Program Files (x86)\\MKVtoolnix\\mkvmerge.exe", // Path to File location
                                        "D:\\Users\\HTPC\\Videos\\AudExtract");              // Path to modified files
```

`File SeriesPath = new File("D:\\Users\\HTPC\\Videos\\DualAudio"); // Path to files you want to modify`


## What it does
It will scan the SeriesPath and get the info about the tracks from each file. it then displays the info and asks what you want to do with it. It will save that info for that set of tracks and use it on tracks that match. at the end it outputs a batch file with all the commands base on the selection. you can just run it or modify it first for any edge cases that you are aware of. 
