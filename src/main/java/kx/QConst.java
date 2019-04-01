package kx;

public class QConst {

    public static int SIMPLE = 0;
    public static int ARRAY = 1;
    public static int OARRAY = 2;
    public static int COMPLEX = 3;
    public static int OTHER = 4;


    public static final int CALLTYPE_ASYNC = 0;
    public static final int CALLTYPE_SYNC = 1;
    public static final int CALLTYPE_RESPONSE = 2;

    public static final int ARCH_BIGENDIAN= 0;				// All writes are BIG Endian
    public static final int ARCH_LITTLEENDIAN = 1;

    public static final int COMPRESSION_DISABLED = 0;				// All writes are BIG Endian
    public static final int COMPRESSION_ENABLED  = 1;


    public static final int QIPC_HEADER_BYTECOUNT = 8;
    public static final int TPLOG_HEADER_BYTECOUNT = 8;

    private static final int ni_MAXNEGINT = Integer.MIN_VALUE; // ni
    private static final long nj_MAXNEGLONG = Long.MIN_VALUE;  // nj
    private static final double nf_DOUBLENAN = Double.NaN;     // nf

    protected static final java.util.TimeZone tz = java.util.TimeZone.getDefault();

    private static String e_encoding = "ISO-8859-1"; // e
    //private static PrintStream out = System.out;


    private static long MSPERDAY =  86400000L;
    private static long EPOCH_OFFSET = 10957;
    private static long k_MS_EPOCH_OFFSET =  MSPERDAY * EPOCH_OFFSET; // k
    private static long n_MAXNANOS = 1000000000L; 					// n


}
