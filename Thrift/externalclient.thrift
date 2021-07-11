service ExternalClientThriftService {
    string getStatus(),
    string getHistory(1:i32 index)
    string getAllInfo()
}