package edu.dartmouth.cs.myruns6;


class WekaClassifier {

	  public static double classify(Object[] i)
	    throws Exception {

	    double p = Double.NaN;
	    p = WekaClassifier.N5672b5b80(i);
	    return p;
	  }
	  static double N5672b5b80(Object []i) {
	    double p = Double.NaN;
	    if (i[0] == null) {
	      p = 0;
	    } else if (((Double) i[0]).doubleValue() <= 59.418692) {
	      p = 0;
	    } else if (((Double) i[0]).doubleValue() > 59.418692) {
	    p = WekaClassifier.N4f86f5f1(i);
	    } 
	    return p;
	  }
	  static double N4f86f5f1(Object []i) {
	    double p = Double.NaN;
	    if (i[0] == null) {
	      p = 1;
	    } else if (((Double) i[0]).doubleValue() <= 452.296058) {
	    p = WekaClassifier.N5257c8852(i);
	    } else if (((Double) i[0]).doubleValue() > 452.296058) {
	    p = WekaClassifier.N6214b0f34(i);
	    } 
	    return p;
	  }
	  static double N5257c8852(Object []i) {
	    double p = Double.NaN;
	    if (i[3] == null) {
	      p = 1;
	    } else if (((Double) i[3]).doubleValue() <= 61.532001) {
	      p = 1;
	    } else if (((Double) i[3]).doubleValue() > 61.532001) {
	    p = WekaClassifier.N12dbdff3(i);
	    } 
	    return p;
	  }
	  static double N12dbdff3(Object []i) {
	    double p = Double.NaN;
	    if (i[2] == null) {
	      p = 1;
	    } else if (((Double) i[2]).doubleValue() <= 72.858782) {
	      p = 1;
	    } else if (((Double) i[2]).doubleValue() > 72.858782) {
	      p = 2;
	    } 
	    return p;
	  }
	  static double N6214b0f34(Object []i) {
	    double p = Double.NaN;
	    if (i[64] == null) {
	      p = 2;
	    } else if (((Double) i[64]).doubleValue() <= 19.779505) {
	    p = WekaClassifier.N3c6d53d35(i);
	    } else if (((Double) i[64]).doubleValue() > 19.779505) {
	      p = 2;
	    } 
	    return p;
	  }
	  static double N3c6d53d35(Object []i) {
	    double p = Double.NaN;
	    if (i[4] == null) {
	      p = 2;
	    } else if (((Double) i[4]).doubleValue() <= 37.726902) {
	    p = WekaClassifier.N2d9a60a36(i);
	    } else if (((Double) i[4]).doubleValue() > 37.726902) {
	    p = WekaClassifier.N667b86a08(i);
	    } 
	    return p;
	  }
	  static double N2d9a60a36(Object []i) {
	    double p = Double.NaN;
	    if (i[6] == null) {
	      p = 1;
	    } else if (((Double) i[6]).doubleValue() <= 8.207564) {
	    p = WekaClassifier.N723e84b87(i);
	    } else if (((Double) i[6]).doubleValue() > 8.207564) {
	      p = 2;
	    } 
	    return p;
	  }
	  static double N723e84b87(Object []i) {
	    double p = Double.NaN;
	    if (i[2] == null) {
	      p = 1;
	    } else if (((Double) i[2]).doubleValue() <= 113.152034) {
	      p = 1;
	    } else if (((Double) i[2]).doubleValue() > 113.152034) {
	      p = 2;
	    } 
	    return p;
	  }
	  static double N667b86a08(Object []i) {
	    double p = Double.NaN;
	    if (i[4] == null) {
	      p = 1;
	    } else if (((Double) i[4]).doubleValue() <= 63.116754) {
	    p = WekaClassifier.N37c3a6f09(i);
	    } else if (((Double) i[4]).doubleValue() > 63.116754) {
	      p = 2;
	    } 
	    return p;
	  }
	  static double N37c3a6f09(Object []i) {
	    double p = Double.NaN;
	    if (i[0] == null) {
	      p = 1;
	    } else if (((Double) i[0]).doubleValue() <= 539.511858) {
	      p = 1;
	    } else if (((Double) i[0]).doubleValue() > 539.511858) {
	    p = WekaClassifier.Ne04f32a10(i);
	    } 
	    return p;
	  }
	  static double Ne04f32a10(Object []i) {
	    double p = Double.NaN;
	    if (i[8] == null) {
	      p = 1;
	    } else if (((Double) i[8]).doubleValue() <= 10.842532) {
	      p = 1;
	    } else if (((Double) i[8]).doubleValue() > 10.842532) {
	      p = 2;
	    } 
	    return p;
	  }
	}
