package adsbrecorder.jni;

import java.io.Serializable;

/**
 * Derived from struct aircraft in dump1090.c
 * @author Ye Wang
 */
public class Aircraft implements Serializable {
    private static final long serialVersionUID = -2980336122991081538L;

    private int addressICAO;
    private String flightNumber;
    private double latitude;
    private double longitude;
    private int altitude;
    private int velocity;
    private int heading;
    private long lastTimeSeen;
    private long messageCounter;
    private int oddCprlat;
    private int oddCprlon;
    private int evenCprlat;
    private int evenCprlon;
    private long oddCprtime;
    private long evenCprtime;

    public Aircraft() {
    }

    public int getAddressICAO() {
        return addressICAO;
    }

    public void setAddressICAO(int addressICAO) {
        this.addressICAO = addressICAO;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        if (flightNumber != null)
            this.flightNumber = flightNumber.trim();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public long getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen(long lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }

    public long getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(long messageCounter) {
        this.messageCounter = messageCounter;
    }

    public int getOddCprlat() {
        return oddCprlat;
    }

    public void setOddCprlat(int oddCprlat) {
        this.oddCprlat = oddCprlat;
    }

    public int getOddCprlon() {
        return oddCprlon;
    }

    public void setOddCprlon(int oddCprlon) {
        this.oddCprlon = oddCprlon;
    }

    public int getEvenCprlat() {
        return evenCprlat;
    }

    public void setEvenCprlat(int evenCprlat) {
        this.evenCprlat = evenCprlat;
    }

    public int getEvenCprlon() {
        return evenCprlon;
    }

    public void setEvenCprlon(int evenCprlon) {
        this.evenCprlon = evenCprlon;
    }

    public long getOddCprtime() {
        return oddCprtime;
    }

    public void setOddCprtime(long oddCprtime) {
        this.oddCprtime = oddCprtime;
    }

    public long getEvenCprtime() {
        return evenCprtime;
    }

    public void setEvenCprtime(long evenCprtime) {
        this.evenCprtime = evenCprtime;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        result = prime * result + addressICAO;
        result = prime * result + altitude;
        result = prime * result + evenCprlat;
        result = prime * result + evenCprlon;
        result = prime * result + (int) (evenCprtime ^ (evenCprtime >>> 32));
        result = prime * result + ((flightNumber == null) ? 0 : flightNumber.hashCode());
        result = prime * result + heading;
        result = prime * result + (int) (lastTimeSeen ^ (lastTimeSeen >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (messageCounter ^ (messageCounter >>> 32));
        result = prime * result + oddCprlat;
        result = prime * result + oddCprlon;
        result = prime * result + (int) (oddCprtime ^ (oddCprtime >>> 32));
        result = prime * result + velocity;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Aircraft other = (Aircraft) obj;
        if (addressICAO != other.addressICAO)
            return false;
        if (altitude != other.altitude)
            return false;
        if (evenCprlat != other.evenCprlat)
            return false;
        if (evenCprlon != other.evenCprlon)
            return false;
        if (evenCprtime != other.evenCprtime)
            return false;
        if (flightNumber == null) {
            if (other.flightNumber != null)
                return false;
        } else if (!flightNumber.equals(other.flightNumber))
            return false;
        if (heading != other.heading)
            return false;
        if (lastTimeSeen != other.lastTimeSeen)
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (messageCounter != other.messageCounter)
            return false;
        if (oddCprlat != other.oddCprlat)
            return false;
        if (oddCprlon != other.oddCprlon)
            return false;
        if (oddCprtime != other.oddCprtime)
            return false;
        if (velocity != other.velocity)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Aircraft [addressICAO=" + addressICAO + ", flightNumber=" + flightNumber + ", latitude=" + latitude
                + ", longitude=" + longitude + ", altitude=" + altitude + ", velocity=" + velocity + ", heading="
                + heading + ", lastTimeSeen=" + lastTimeSeen + ", messageCounter=" + messageCounter + ", oddCprlat="
                + oddCprlat + ", oddCprlon=" + oddCprlon + ", evenCprlat=" + evenCprlat + ", evenCprlon=" + evenCprlon
                + ", oddCprtime=" + oddCprtime + ", evenCprtime=" + evenCprtime + "]";
    }
}
