public record TesterConfig(long seed, int initialSize,
        int operationCount, double insertProbability, int checkEvery) {
}