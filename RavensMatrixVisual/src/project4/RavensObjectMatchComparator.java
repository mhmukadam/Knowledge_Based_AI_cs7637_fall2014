package project4;

import java.util.Comparator;

public class RavensObjectMatchComparator implements Comparator<RavensObjectMatch>
{
    @Override
    public int compare(RavensObjectMatch x, RavensObjectMatch y)
    {
        if (x.score > y.score)
        {
            return -1;
        }
        if (x.score < y.score)
        {
            return 1;
        }
        return 0;
    }
}