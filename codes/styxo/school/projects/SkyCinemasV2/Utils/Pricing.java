package codes.styxo.school.projects.SkyCinemasV2.Utils;

//Class to store the pricing of seats and other price-related values
public class Pricing {
    //The GST rate on the total cost
    public final static double GSTpercent = 28;
    //The pricing of seats based on position
    public final static double[] pricingPerSeat = {
            //Silver
            120,
            //Gold
            140,
            //Platinum
            160,
            //Recliner
            250,
    };

    //Method to get the ticket cost of a seat as an array of length 2
    //0 will store the total cost
    //1 will store the GST
    public static double[] getTicketCost(double baseCost, int sectionIndex) {
        double[] res = new double[2];
        //Pricing per seat is
        //Base cost * number of seats + pricing per seat * number of seats + GST
        res[0] = baseCost
                + pricingPerSeat[Math.min(pricingPerSeat.length - 1, Math.max(0, sectionIndex))];
        res[1] = GSTpercent / 100 * res[0];
        return res;
    }
}
