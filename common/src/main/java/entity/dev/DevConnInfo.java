package entity.dev;

/**
 *  设备连接信息
 * @author Gmq
 * @date 2019-01-24 18:19
 **/
public class DevConnInfo {
    private boolean cleanStart;

    private String clientId;

    private String connectedAt;

    private Integer heapSize;

    private String ipaddress;

    private boolean isBridge;

    private boolean isSuper;

    private Integer keepalive;

    private Integer mailboxLen;
    private String mountpoint;
    private String node;
    private String peercert;
    private Integer port;
    private String protoName;
    private Integer protoVer;
    private Integer recvCnt;
    private Integer recvMsg;
    private Integer recvOct;
    private Integer recvPkt;
    private Integer reductions;
    private Integer sendCnt;
    private Integer sendMsg;
    private Integer sendOct;
    private Integer sendPend;
    private Integer sendPkt;
    private String username;
    private String zone;

    public boolean isCleanStart() {
        return cleanStart;
    }

    public void setCleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(String connectedAt) {
        this.connectedAt = connectedAt;
    }

    public Integer getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(Integer heapSize) {
        this.heapSize = heapSize;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public boolean isBridge() {
        return isBridge;
    }

    public void setBridge(boolean bridge) {
        isBridge = bridge;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }

    public Integer getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(Integer keepalive) {
        this.keepalive = keepalive;
    }

    public Integer getMailboxLen() {
        return mailboxLen;
    }

    public void setMailboxLen(Integer mailboxLen) {
        this.mailboxLen = mailboxLen;
    }

    public String getMountpoint() {
        return mountpoint;
    }

    public void setMountpoint(String mountpoint) {
        this.mountpoint = mountpoint;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPeercert() {
        return peercert;
    }

    public void setPeercert(String peercert) {
        this.peercert = peercert;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtoName() {
        return protoName;
    }

    public void setProtoName(String protoName) {
        this.protoName = protoName;
    }

    public Integer getProtoVer() {
        return protoVer;
    }

    public void setProtoVer(Integer protoVer) {
        this.protoVer = protoVer;
    }

    public Integer getRecvCnt() {
        return recvCnt;
    }

    public void setRecvCnt(Integer recvCnt) {
        this.recvCnt = recvCnt;
    }

    public Integer getRecvMsg() {
        return recvMsg;
    }

    public void setRecvMsg(Integer recvMsg) {
        this.recvMsg = recvMsg;
    }

    public Integer getRecvOct() {
        return recvOct;
    }

    public void setRecvOct(Integer recvOct) {
        this.recvOct = recvOct;
    }

    public Integer getRecvPkt() {
        return recvPkt;
    }

    public void setRecvPkt(Integer recvPkt) {
        this.recvPkt = recvPkt;
    }

    public Integer getReductions() {
        return reductions;
    }

    public void setReductions(Integer reductions) {
        this.reductions = reductions;
    }

    public Integer getSendCnt() {
        return sendCnt;
    }

    public void setSendCnt(Integer sendCnt) {
        this.sendCnt = sendCnt;
    }

    public Integer getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(Integer sendMsg) {
        this.sendMsg = sendMsg;
    }

    public Integer getSendOct() {
        return sendOct;
    }

    public void setSendOct(Integer sendOct) {
        this.sendOct = sendOct;
    }

    public Integer getSendPend() {
        return sendPend;
    }

    public void setSendPend(Integer sendPend) {
        this.sendPend = sendPend;
    }

    public Integer getSendPkt() {
        return sendPkt;
    }

    public void setSendPkt(Integer sendPkt) {
        this.sendPkt = sendPkt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
