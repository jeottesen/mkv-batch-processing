# mkv-batch-processing
I made this in 2013 to process large amounts of mkv files easier.
I wanted to remove extra audio tracks and add external subs for files. It is also good for setting default tracks. 

**ONLY COMPATIBLE WITH MKVMERGE VERSIONS 19 OR LESS**
In newer versions the flag --identify-verbose has been deprecated. Since this program relies on that flag the newer versions will not work.
use the --mkvmerge flag to specify the version of mkvmerge this program will use.

## Arguments
I have now added command line arguments.

```
Usage: mkv-batch-processor from_path to_path [-output file] [--mkvmerge path]
    from_path       - Path to folder you want to process
    to_path         - Path to folder you want the files to end up
    --output file    - Path to output the script file (Default: ./mkv_batch.bat or ./mkv_batch.sh)
    --mkvmerge path - Path to mkvmerge executable

    It will scan the [from_path] and get the info about the tracks from each file. 
    it then displays the info and asks what you want to do with it. It will save 
    that info for that set of tracks and use it on tracks that match. at the end it
    outputs a batch file with all the commands based on the selection.
```

## What it does
It will scan the SeriesPath and get the info about the tracks from each file. it then displays the info and asks what you want to do with it. It will save that info for that set of tracks and use it on tracks that match. at the end it outputs a batch file with all the commands base on the selection. you can just run it or modify it first for any edge cases that you are aware of. 
