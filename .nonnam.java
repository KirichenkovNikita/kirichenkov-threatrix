private void readWebSocketFrame() throws IOException {
    try {
        Frame frame = Frame.read(this.inputStream);
        if (frame.getType() == Frame.Type.PING) {
            writeWebSocketFrame(new Frame(Frame.Type.PONG));
        }
        else if (frame.getType() == Frame.Type.CLOSE) {
            throw new ConnectionClosedException();
        }
        else if (frame.getType() == Frame.Type.TEXT) {
            logger.debug(LogMessage.format("Received LiveReload text frame %s", frame));
        }
        else {
            throw new IOException("Unexpected Frame Type " + frame.getType());
        }
    }
    catch (SocketTimeoutException ex) {
        writeWebSocketFrame(new Frame(Frame.Type.PING));
        Frame frame = Frame.read(this.inputStream);
        if (frame.getType() != Frame.Type.PONG) {
            throw new IllegalStateException("No Pong");
        }
    }
}

/**
 * Trigger livereload for the client using this connection.
 * @throws IOException in case of I/O errors
 */
void triggerReload() throws IOException {
    if (this.webSocket) {
        logger.debug("Triggering LiveReload");
        writeWebSocketFrame(new Frame("{\"command\":\"reload\",\"path\":\"/\"}"));
    }
}

private void complementComponentLicenses(ProjectManifest projectManifest) {
    // fill all components with license data
    List<CompletableFuture<Void>> componentLicenseLookUpTasks = new LinkedList<>();
    AtomicInteger componentsWithNoLicensesCnt = new AtomicInteger();
    projectManifest.getAllComponents().forEach(
            component -> {
                componentLicenseLookUpTasks.add(
                        CompletableFuture.runAsync(setLicensesToComponentFunction(componentsWithNoLicensesCnt, component), componentLicenseLookupExecutor)
                );
            }
    );
    componentLicenseLookUpTasks.forEach(CompletableFuture::join);
    logger.info("Components with no licenses: {} of {}", componentsWithNoLicensesCnt.get(), projectManifest.getAllComponents().size());
}