package SimpleGA;

public class Algorithm {

    /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    /* Public methods */

    // Evolve a population
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        for (int i = elitismOffset; i < pop.size(); i++) {
            Individual indiv1 = FPSelection(pop);
            Individual indiv2 = FPSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover individuals
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    // onePointCrossover individuals
    private static Individual onePointCrossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();

        int randomId = (int) (Math.random() * (indiv1.size() + 1)); // there is difference index=0 & index=size
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (i < randomId) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }

        return newSol;
    }

    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i, gene);
            }
        }
    }

    // Select individuals for crossover
    private static Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        Individual fittest = tournament.getFittest();
        return fittest;
    }

    // Select individuals for crossover
    private static Individual FPSelection(Population pop) {
        // setup for FPS
        calcPMating(pop);
        double roll = Math.random();
        Individual selected = null;
        for (Individual individual : pop.individuals) {
            double lastProbability = 0;
            if (roll >= lastProbability && roll < individual.getAccProbability()) {
                selected = individual;
                return selected;
            }
            lastProbability = individual.getAccProbability();
        }
        return selected;
    }

    public static void calcPMating(Population pop) {
        double accProbability = 0;
        for (Individual individual : pop.individuals) {
            individual.setProbability(individual.getFitness()/pop.getTotalFitness());
            accProbability =+ individual.getProbability();
            individual.setAccProbability(accProbability);
        }
    }

}