service ClientThriftService {
    bool shutDown(1:bool shutDown),
    bool changePower(1:i32 amount)
}