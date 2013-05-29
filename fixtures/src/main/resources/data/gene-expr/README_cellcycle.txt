
This is the legend to the tab-delimited text file for the subset of 
384 genes from the yeast cell cycle data [Cho et al.]
that Ka Yee used in her thesis. The first column is the ORF, the second
column is the "phase" (explained in point 2 below).
Here is how Ka Yee obtained the subset:

1. the full data set from the stanford yeast cell cycle web site
   was downloaded:
   http://171.65.26.52/yeast_cell_cycle/cellcycle.html

2. Follow the link "Functional Categories of Genes" on the page.
   Extract all the genes that were assigned a "phase". We call
   each "phase" a "Main Group". Here is the key:
   "Main Group"	  key
   1		  early G1
   2		  late G1
   3		  S
   4		  G2
   5		  M
   6		  multi-phase (assigned to more than 1 phase)

   This step gives a subset of approx 420 genes.

3. Remove all the rows with "Main Group" = 6 (ie, now, all the
   remaining genes peak in only 1 phase).
   This gives a subset of 386 genes.

4. Remove all the rows with any negative entries. This is because 
   we don't know how to interpret negative values without further
   information.
   This gives a subset of 384 genes. The data we attached are the
   "raw" values, before any data transformations.

Disclaimer: We do NOT claim that this is a "good" (unbiased) 
subset of the data.  We did this in order to assess the cluster 
quality with the external criterion (the 5 phases). We believe
that we did a reasonable job, but not necessarily perfect. 
For example, I believe that in some follow-up papers after
Cho et al, it was mentioned that two of the time points were
unreliable.  

