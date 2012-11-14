/*******************************************************************************
 * Copyright (c) 1999-2005 The Institute for Genomic Research (TIGR).
 * Copyright (c) 2005-2008, the Dana-Farber Cancer Institute (DFCI), 
 * J. Craig Venter Institute (JCVI) and the University of Washington.
 * All rights reserved.
 *******************************************************************************/
/*
 * $RCSfile: Maths.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2003-08-21 21:04:23 $
 * $Author: braisted $
 * $State: Exp $
 */
package org.clueminer.math;

public class Maths {
    /** sqrt(a^2 + b^2) without under/overflow. **/
    public static float hypot(float a, float b) {
	double r;
	if (Math.abs(a) > Math.abs(b)) {
	    r = b/a;
	    r = (float)(Math.abs(a)*Math.sqrt(1+r*r));
	} else if (b != 0) {
	    r = a/b;
	    r = (float)(Math.abs(b)*Math.sqrt(1+r*r));
	} else {
	    r = 0.0f;
	}
	return(float)r;
    }
}
