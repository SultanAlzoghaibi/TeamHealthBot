terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

# Fetch availability zones
data "aws_availability_zones" "available" {}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "4.0.0"

  name = "teamhealth-vpc"
  cidr = "10.0.0.0/16"

  azs             = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnets  = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnets = ["10.0.11.0/24", "10.0.12.0/24"]
}

module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  version         = "20.0.0"
  cluster_name    = "teamhealth"
  cluster_version = "1.27"

  vpc_id     = module.vpc.vpc_id
  subnets    = module.vpc.private_subnets

  node_groups = {
    default = {
      desired_capacity = 2
      min_capacity     = 1
      max_capacity     = 3
      instance_types   = ["t3.medium"]
    }
  }
}


output "kubeconfig" {
  value     = module.eks.kubeconfig
  sensitive = true
}