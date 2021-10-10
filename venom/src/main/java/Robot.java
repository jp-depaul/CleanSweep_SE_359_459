public class Robot {
  private VirtualHome virtualHome;
  private final int CHARGE_CAPACITY = 250;
  private final int DIRT_CAPACITY = 50;
  private int chargeLevel;
  private int dirtLevel;

  public Robot(VirtualHome virtualHome) {
    this.virtualHome = virtualHome;
  }

  private void scanCell() {

  }
}
