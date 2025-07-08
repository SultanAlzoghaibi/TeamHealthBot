resource "aws_security_group" "rds_sg" {
  name        = "rds_sg"
  description = "Allow EKS to access RDS"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "PostgreSQL from EKS SG"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    security_groups = [aws_security_group.eks_sg.id] # Let EKS access
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "RDS SG"
  }
}

resource "aws_db_subnet_group" "rds_subnets" {
  name       = "team-health-db-subnet-group"
  subnet_ids = [
    aws_subnet.private_subnet_1.id,
    aws_subnet.private_subnet_2.id,
    aws_subnet.private_subnet_3.id,
  ]
  tags = {
    Name = "TeamHealth RDS Subnet Group"
  }
}


resource "aws_db_instance" "postgres" {
  identifier              = "team-health-db"
  engine                  = "postgres"
  instance_class          = "db.m6g.xlarge"
  allocated_storage       = 20
  db_name                 = "postgres"
  username                = "sultanalzoghaibi"
  password                = "yourpassword123" # ← use env var or tfvars in real prod
  publicly_accessible     = false
  multi_az                = false
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  db_subnet_group_name    = aws_db_subnet_group.rds_subnets.name
  skip_final_snapshot     = true
  storage_encrypted       = true

  tags = {
    Name = "TeamHealth PostgreSQL DB"
  }
}

resource "aws_elasticache_subnet_group" "redis_subnets" {
  name       = "team-health-redis-subnet-group"
  subnet_ids = [
    aws_subnet.private_subnet_1.id,
    aws_subnet.private_subnet_2.id,
    aws_subnet.private_subnet_3.id,
  ]

  tags = {
    Name = "TeamHealth Redis Subnet Group"
  }
}

# resource "aws_db_instance" "read_replica" {
#   identifier              = "teamhealth-read"
#   replicate_source_db     = aws_db_instance.postgres.id
#   instance_class          = "db.t3.medium"
#   publicly_accessible     = false
#   skip_final_snapshot     = true
#   depends_on              = [aws_db_instance.postgres]
# }

resource "aws_elasticache_replication_group" "redis" {
  replication_group_id          = "team-health-redis"
  engine                        = "redis"
  engine_version                = "7.0"
  node_type                     = "cache.m6g.xlarge"
  subnet_group_name             = aws_elasticache_subnet_group.redis_subnets.name
  security_group_ids            = [aws_security_group.rds_sg.id] # reuse SG for simplicity
  automatic_failover_enabled    = false
  multi_az_enabled              = false
  at_rest_encryption_enabled    = true
  transit_encryption_enabled    = true

  tags = {
    Name = "TeamHealth Redis Cluster"
  }
  description = "redis "
}